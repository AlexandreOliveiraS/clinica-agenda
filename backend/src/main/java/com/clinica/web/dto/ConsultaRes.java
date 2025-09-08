package com.clinica.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaRes(UUID id, UUID pacienteId, UUID fisioId, LocalDateTime inicio, LocalDateTime fim) {}
