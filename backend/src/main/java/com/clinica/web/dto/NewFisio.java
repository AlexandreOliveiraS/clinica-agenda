package com.clinica.web.dto;

import jakarta.validation.constraints.NotBlank;

public record NewFisio(@NotBlank String nome, String registro) {}
