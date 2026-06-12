package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    
    List<Inscripcion> findByUsuarioId(Long usuarioId);
    
    List<Inscripcion> findByCursoId(Long cursoId);
    
    List<Inscripcion> findByEstado(String estado);
    
    Optional<Inscripcion> findByReservaId(Long reservaId);
    
    List<Inscripcion> findByUsuarioIdAndEstado(Long usuarioId, String estado);
    
    long countByEstado(String estado);
} 