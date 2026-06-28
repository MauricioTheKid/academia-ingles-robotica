package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    
    List<Horario> findByCursoId(Long cursoId);
    
    List<Horario> findByDiaSemana(String diaSemana);
}