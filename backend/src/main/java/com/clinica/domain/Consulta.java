package com.clinica.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="consultas",
  uniqueConstraints = {
    @UniqueConstraint(name="uq_fisio_inicio", columnNames={"fisioterapeuta_id","inicio"}),
    @UniqueConstraint(name="uq_idem", columnNames={"idempotencyKey"})
  })
public class Consulta {
  @Id @UuidGenerator
  private UUID id;

  @ManyToOne(optional=false) private Fisioterapeuta fisioterapeuta;
  @ManyToOne(optional=false) private Paciente paciente;

  private LocalDateTime inicio;
  private LocalDateTime fim;

  @Column(length=64) private String idempotencyKey;

  public UUID getId() { return id; } 
  public Fisioterapeuta getFisioterapeuta() { return fisioterapeuta; }
  public void setFisioterapeuta(Fisioterapeuta f) { this.fisioterapeuta = f; }
  public Paciente getPaciente() { return paciente; }
  public void setPaciente(Paciente p) { this.paciente = p; }
  public LocalDateTime getInicio() { return inicio; }
  public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
  public LocalDateTime getFim() { return fim; }
  public void setFim(LocalDateTime fim) { this.fim = fim; }
  public String getIdempotencyKey() { return idempotencyKey; }
  public void setIdempotencyKey(String k) { this.idempotencyKey = k; }
}
