package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    
    List<Horario> findByCursoId(Long cursoId);
    
    List<Horario> findByDiaSemana(String diaSemana);
}