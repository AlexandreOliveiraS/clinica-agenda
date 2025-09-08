package com.clinica.web.dto;

import jakarta.validation.constraints.NotBlank;

public record NewPaciente(@NotBlank String nome, String telefone) {}
