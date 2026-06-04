package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    List<Reserva> findByUsuarioId(Long usuarioId);
    
    List<Reserva> findByHorarioId(Long horarioId);
    
    List<Reserva> findByEstado(String estado);
    
    List<Reserva> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}