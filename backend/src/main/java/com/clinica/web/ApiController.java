package com.clinica.web;

import com.clinica.domain.Consulta;
import com.clinica.domain.Fisioterapeuta;
import com.clinica.domain.Paciente;
import com.clinica.repo.ConsultaRepository;
import com.clinica.repo.FisioterapeutaRepository;
import com.clinica.repo.PacienteRepository;
import com.clinica.service.AgendaService;
import com.clinica.service.Errors;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class ApiController {

  private final PacienteRepository pacientes;
  private final FisioterapeutaRepository fisios;
  private final ConsultaRepository consultas;
  private final AgendaService agenda;

  public ApiController(PacienteRepository pacientes,
                       FisioterapeutaRepository fisios,
                       ConsultaRepository consultas,
                       AgendaService agenda) {
    this.pacientes = pacientes;
    this.fisios = fisios;
    this.consultas = consultas;
    this.agenda = agenda;
  }

  // ======== DTOs (records) ========
  public record NewPaciente(@NotBlank String nome, String telefone) {}
  public record NewFisio(@NotBlank String nome, String registro) {}
  public record NewConsulta(@NotNull UUID pacienteId,
                            @NotNull UUID fisioId,
                            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                            @Min(15) int duracaoMin) {}
  public record ConsultaRes(UUID id, UUID pacienteId, UUID fisioId, LocalDateTime inicio, LocalDateTime fim) {}

  // ======== Pacientes ========
  @PostMapping("/pacientes")
  public Paciente createPaciente(@RequestBody @Valid NewPaciente req) {
    Paciente p = new Paciente();
    p.setNome(req.nome());
    p.setTelefone(req.telefone());
    return pacientes.save(p);
  }

  @GetMapping("/pacientes")
  public List<Paciente> listPacientes() {
    return pacientes.findAll();
  }

  // ======== Fisioterapeutas ========
  @PostMapping("/fisios")
  public Fisioterapeuta createFisio(@RequestBody @Valid NewFisio req) {
    Fisioterapeuta f = new Fisioterapeuta();
    f.setNome(req.nome());
    f.setRegistro(req.registro());
    return fisios.save(f);
  }

  @GetMapping("/fisios")
  public List<Fisioterapeuta> listFisios() {
    return fisios.findAll();
  }

  // ======== Consultas ========
  @PostMapping("/consultas")
  public ResponseEntity<ConsultaRes> agendar(
      @RequestHeader(name = "Idempotency-Key", required = false) String idemKey,
      @RequestBody @Valid NewConsulta req) {

    Consulta c = agenda.agendar(req.pacienteId(), req.fisioId(), req.inicio(), req.duracaoMin(), idemKey);

    ConsultaRes res = new ConsultaRes(
        c.getId(),
        c.getPaciente().getId(),
        c.getFisioterapeuta().getId(),
        c.getInicio(),
        c.getFim()
    );

    return ResponseEntity.status(HttpStatus.CREATED)
        .header(HttpHeaders.LOCATION, "/api/consultas/" + c.getId())
        .body(res);
  }

  @GetMapping("/consultas")
public List<ConsultaRes> listar(@RequestParam UUID fisioId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia) {

  LocalDateTime inicio = dia.atStartOfDay();
  LocalDateTime fim = inicio.plusDays(1);

  return consultas.findAll().stream()
      .filter(c -> c.getFisioterapeuta().getId().equals(fisioId)
                && !c.getInicio().isAfter(fim)
                && !c.getFim().isBefore(inicio))
      .map(c -> new ConsultaRes(
          c.getId(),
          c.getPaciente().getId(),
          c.getFisioterapeuta().getId(),
          c.getInicio(),
          c.getFim()))
      .collect(Collectors.toList());   // <-- aqui
}

  // ======== Handlers de erro ========
  @ExceptionHandler(Errors.NotFound.class)
  public ResponseEntity<Map<String, String>> notFound(Errors.NotFound e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler(Errors.BusinessRule.class)
  public ResponseEntity<Map<String, String>> business(Errors.BusinessRule e) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler(Errors.Conflict.class)
  public ResponseEntity<Map<String, String>> conflict(Errors.Conflict e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
  }
}
