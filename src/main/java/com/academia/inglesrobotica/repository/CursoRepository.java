package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    
    List<Curso> findByActivoTrue();
    
    List<Curso> findByNivelId(Long nivelId);
    
    List<Curso> findByNombreContainingIgnoreCase(String nombre);
}