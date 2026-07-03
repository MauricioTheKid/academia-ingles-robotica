package com.academia.inglesrobotica.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academia.inglesrobotica.model.Calificacion;
import com.academia.inglesrobotica.model.Inscripcion;
import com.academia.inglesrobotica.repository.CalificacionRepository;

@Service
public class CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private InscripcionService inscripcionService;

    public List<Calificacion> findAll() {
        return calificacionRepository.findAll();
    }

    public Optional<Calificacion> findById(Long id) {
        return calificacionRepository.findById(id);
    }

    public Optional<Calificacion> findByInscripcionId(Long inscripcionId) {
        return calificacionRepository.findByInscripcionId(inscripcionId);
    }

    public List<Calificacion> findByCursoId(Long cursoId) {
        return calificacionRepository.findByCursoId(cursoId);
    }

    public List<Calificacion> findByUsuarioId(Long usuarioId) {
        return calificacionRepository.findByUsuarioId(usuarioId);
    }

    public List<Calificacion> findByInscripcionActiva() {
        return calificacionRepository.findByInscripcionActiva();
    }

    @Transactional
    public Calificacion guardarOActualizar(Long inscripcionId, Calificacion calificacion) {
        System.out.println("=== DEBUG: guardarOActualizar ===");
        System.out.println("InscripcionId: " + inscripcionId);
        System.out.println("Nota Parcial 1: " + calificacion.getNotaParcial1());
        System.out.println("Nota Parcial 2: " + calificacion.getNotaParcial2());
        System.out.println("Nota Parcial 3: " + calificacion.getNotaParcial3());
        System.out.println("Nota Parcial 4: " + calificacion.getNotaParcial4());
        System.out.println("Nota Proyecto: " + calificacion.getNotaProyecto());
        System.out.println("Nota Examen Final: " + calificacion.getNotaExamenFinal());
        System.out.println("Observaciones: " + calificacion.getObservaciones());

        Inscripcion inscripcion = inscripcionService.findById(inscripcionId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada con ID: " + inscripcionId));

        System.out.println("Inscripción encontrada: " + inscripcion.getId());

        // Buscar si ya existe una calificación para esta inscripción
        Optional<Calificacion> existente = calificacionRepository.findByInscripcionId(inscripcionId);

        Calificacion calif;
        if (existente.isPresent()) {
            calif = existente.get();
            System.out.println("Actualizando calificación existente ID: " + calif.getId());
        } else {
            calif = new Calificacion();
            calif.setInscripcion(inscripcion);
            System.out.println("Creando nueva calificación");
        }

        // Actualizar notas
        calif.setNotaParcial1(calificacion.getNotaParcial1());
        calif.setNotaParcial2(calificacion.getNotaParcial2());
        calif.setNotaParcial3(calificacion.getNotaParcial3());
        calif.setNotaParcial4(calificacion.getNotaParcial4());
        calif.setNotaProyecto(calificacion.getNotaProyecto());
        calif.setNotaExamenFinal(calificacion.getNotaExamenFinal());
        calif.setObservaciones(calificacion.getObservaciones());
        calif.setFechaActualizacion(LocalDateTime.now());

        Calificacion saved = calificacionRepository.save(calif);
        System.out.println("✅ Calificación guardada con ID: " + saved.getId());
        System.out.println("Promedio calculado: " + saved.getPromedio());
        System.out.println("Estado: " + saved.getEstado());

        return saved;
    }

    @Transactional
    public void deleteById(Long id) {
        calificacionRepository.deleteById(id);
    }

    public long countAprobados() {
        return calificacionRepository.findAll().stream()
                .filter(c -> "Aprobado".equals(c.getEstado()))
                .count();
    }

    public long countReprobados() {
        return calificacionRepository.findAll().stream()
                .filter(c -> "Reprobado".equals(c.getEstado()))
                .count();
    }
}