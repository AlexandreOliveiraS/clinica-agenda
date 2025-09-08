package com.clinica.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
public class Fisioterapeuta {
  @Id @UuidGenerator
  private UUID id;
  private String nome;
  private String registro;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public String getRegistro() { return registro; }
  public void setRegistro(String registro) { this.registro = registro; }
}
