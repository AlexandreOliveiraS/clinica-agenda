package com.clinica.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
public class Paciente {

  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID id;

  private String nome;
  private String telefone;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }

  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }

  public String getTelefone() { return telefone; }
  public void setTelefone(String telefone) { this.telefone = telefone; }
}
