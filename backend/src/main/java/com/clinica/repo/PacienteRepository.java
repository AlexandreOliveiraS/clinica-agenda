package com.clinica.repo;

import com.clinica.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PacienteRepository extends JpaRepository<Paciente, UUID> {}
