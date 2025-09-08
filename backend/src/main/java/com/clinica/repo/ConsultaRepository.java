package com.clinica.repo;

import com.clinica.domain.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ConsultaRepository extends JpaRepository<Consulta, UUID> {
  boolean existsByFisioterapeuta_IdAndInicioLessThanAndFimGreaterThan(UUID fisioId,
                                                                      LocalDateTime fim,
                                                                      LocalDateTime inicio);
  boolean existsByIdempotencyKey(String idempotencyKey);
}
