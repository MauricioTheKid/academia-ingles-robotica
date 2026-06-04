package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NivelRepository extends JpaRepository<Nivel, Long> {
    
    Optional<Nivel> findByNombre(String nombre);
}