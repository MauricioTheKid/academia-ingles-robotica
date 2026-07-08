package com.academia.inglesrobotica.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.academia.inglesrobotica.model.Curso;
import com.academia.inglesrobotica.model.ProfesorCurso;
import com.academia.inglesrobotica.repository.ProfesorCursoRepository;

@Service
public class ProfesorCursoService {

    @Autowired
    private ProfesorCursoRepository profesorCursoRepository;

    // Obtener los IDs de cursos asignados a un profesor
    public List<Long> findCursoIdsByProfesorId(Long profesorId) {
        List<ProfesorCurso> asignaciones = profesorCursoRepository.findByProfesorId(profesorId);
        return asignaciones.stream()
                .map(pc -> pc.getCurso().getId())
                .collect(Collectors.toList());
    }

    // Obtener los cursos asignados a un profesor
    public List<Curso> findCursosByProfesorId(Long profesorId) {
        List<ProfesorCurso> asignaciones = profesorCursoRepository.findByProfesorId(profesorId);
        return asignaciones.stream()
                .map(ProfesorCurso::getCurso)
                .collect(Collectors.toList());
    }

    // Verificar si un profesor tiene un curso asignado
    public boolean isProfesorAsignado(Long profesorId, Long cursoId) {
        return profesorCursoRepository.existsByProfesorIdAndCursoId(profesorId, cursoId);
    }
}