package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    
    // Buscar calificación por inscripción
    Optional<Calificacion> findByInscripcionId(Long inscripcionId);
    
    // Buscar calificaciones por curso (a través de inscripción)
    @Query("SELECT c FROM Calificacion c JOIN c.inscripcion i WHERE i.curso.id = :cursoId")
    List<Calificacion> findByCursoId(@Param("cursoId") Long cursoId);
    
    // Buscar calificaciones por usuario/alumno
    @Query("SELECT c FROM Calificacion c JOIN c.inscripcion i WHERE i.usuario.id = :usuarioId")
    List<Calificacion> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Buscar calificaciones por estado de inscripción
    @Query("SELECT c FROM Calificacion c JOIN c.inscripcion i WHERE i.estado = 'ACTIVA'")
    List<Calificacion> findByInscripcionActiva();
    
    // Contar calificaciones por curso
    @Query("SELECT COUNT(c) FROM Calificacion c JOIN c.inscripcion i WHERE i.curso.id = :cursoId")
    long countByCursoId(@Param("cursoId") Long cursoId);
} 