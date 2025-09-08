package com.clinica.service;

import com.clinica.domain.Consulta;
import com.clinica.domain.Fisioterapeuta;
import com.clinica.domain.Paciente;
import com.clinica.repo.ConsultaRepository;
import com.clinica.repo.FisioterapeutaRepository;
import com.clinica.repo.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AgendaService {
  private final ConsultaRepository consultas;
  private final PacienteRepository pacientes;
  private final FisioterapeutaRepository fisios;

  public AgendaService(ConsultaRepository c, PacienteRepository p, FisioterapeutaRepository f) {
    this.consultas = c; this.pacientes = p; this.fisios = f;
  }

  @Transactional
public Consulta agendar(UUID pacienteId, UUID fisioId, LocalDateTime inicio, int duracaoMin, String idemKey) {
    if (duracaoMin < 15) throw new Errors.BusinessRule("Duração mínima 15 min");

    Paciente paciente = pacientes.findById(pacienteId)
        .orElseThrow(() -> new Errors.NotFound("Paciente não encontrado"));
    Fisioterapeuta fisio = fisios.findById(fisioId)
        .orElseThrow(() -> new Errors.NotFound("Fisioterapeuta não encontrado"));

    if (idemKey != null && !idemKey.isBlank() && consultas.existsByIdempotencyKey(idemKey))
        throw new Errors.Conflict("Consulta já criada (idempotência)");

    LocalDateTime fim = inicio.plusMinutes(duracaoMin);

    if (consultas.existsByFisioterapeuta_IdAndInicioLessThanAndFimGreaterThan(fisioId, fim, inicio))
        throw new Errors.Conflict("Horário indisponível para este fisioterapeuta");

    Consulta c = new Consulta();
    c.setPaciente(paciente);
    c.setFisioterapeuta(fisio);
    c.setInicio(inicio);
    c.setFim(fim);
    c.setIdempotencyKey(idemKey);

    return consultas.save(c);
}

}
