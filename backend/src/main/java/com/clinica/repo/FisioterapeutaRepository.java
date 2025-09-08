package com.clinica.repo;

import com.clinica.domain.Fisioterapeuta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FisioterapeutaRepository extends JpaRepository<Fisioterapeuta, UUID> {}
