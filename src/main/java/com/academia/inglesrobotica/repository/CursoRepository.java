package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    
    List<Curso> findByActivoTrue();
    
    List<Curso> findByNivelId(Long nivelId);
    
    List<Curso> findByNombreContainingIgnoreCase(String nombre);
}