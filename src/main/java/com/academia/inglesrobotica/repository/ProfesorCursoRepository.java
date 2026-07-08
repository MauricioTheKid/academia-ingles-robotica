package com.academia.inglesrobotica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.academia.inglesrobotica.model.ProfesorCurso;

public interface ProfesorCursoRepository extends JpaRepository<ProfesorCurso, Long> {

    // Buscar cursos asignados a un profesor
    List<ProfesorCurso> findByProfesorId(Long profesorId);

    // Buscar profesores asignados a un curso
    List<ProfesorCurso> findByCursoId(Long cursoId);

    // Verificar si un profesor está asignado a un curso
    boolean existsByProfesorIdAndCursoId(Long profesorId, Long cursoId);
}