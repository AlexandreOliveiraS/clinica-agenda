package com.clinica.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record NewConsulta(@NotNull UUID pacienteId,
                          @NotNull UUID fisioId,
                          @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                          @Min(15) int duracaoMin) {}
