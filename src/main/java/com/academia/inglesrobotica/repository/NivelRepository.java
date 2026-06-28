package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NivelRepository extends JpaRepository<Nivel, Long> {
    
    Optional<Nivel> findByNombre(String nombre);
}