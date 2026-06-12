package com.academia.inglesrobotica.service;

import com.academia.inglesrobotica.dto.PagoRequest;
import com.academia.inglesrobotica.model.Inscripcion;
import com.academia.inglesrobotica.model.Pago;
import com.academia.inglesrobotica.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Pago registrarPago(PagoRequest request) {
        System.out.println("=== DEBUG: PagoService.registrarPago ===");
        System.out.println("InscripcionId: " + request.getInscripcionId());
        
        Inscripcion inscripcion = inscripcionService.findById(request.getInscripcionId())
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada con ID: " + request.getInscripcionId()));
        
        System.out.println("Inscripción encontrada - ID: " + inscripcion.getId() + ", Estado actual: " + inscripcion.getEstado());

        // Validar que la inscripción esté en estado PENDIENTE_PAGO
        if (!"PENDIENTE_PAGO".equals(inscripcion.getEstado())) {
            System.out.println("ERROR: Estado incorrecto - Actual: " + inscripcion.getEstado());
            throw new RuntimeException("La inscripción no está pendiente de pago. Estado actual: " + inscripcion.getEstado());
        }

        Pago pago = new Pago();
        pago.setInscripcion(inscripcion);
        pago.setMetodoPago(request.getMetodoPago());
        pago.setReferenciaPago(request.getReferenciaPago());
        pago.setMonto(request.getMonto() != null ? request.getMonto() : inscripcion.getCurso().getPrecio());
        pago.setEstado("PENDIENTE");

        System.out.println("Pago a guardar - Método: " + pago.getMetodoPago() + ", Monto: " + pago.getMonto());

        // Cambiar estado de inscripción a PAGO_VERIFICACION
        inscripcionService.actualizarEstado(inscripcion.getId(), "PAGO_VERIFICACION", 
            "Esperando verificación de pago por administrador");

        Pago saved = pagoRepository.save(pago);
        System.out.println("Pago guardado con ID: " + saved.getId());
        
        return saved;
    }

    @Transactional
    public Pago verificarPago(Long id, boolean aprobado, String observaciones) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        if (aprobado) {
            pago.setEstado("COMPLETADO");
            pago.setFechaPago(LocalDateTime.now());
            // Activar la inscripción
            inscripcionService.actualizarEstado(pago.getInscripcion().getId(), "ACTIVA", observaciones);
            // Enviar email de éxito
            emailService.enviarPagoVerificado(
                pago.getInscripcion().getUsuario().getEmail(),
                true,
                pago.getInscripcion().getCurso().getNombre()
            );
        } else {
            pago.setEstado("RECHAZADO");
            inscripcionService.actualizarEstado(pago.getInscripcion().getId(), "RECHAZADA", observaciones);
            // Enviar email de rechazo
            emailService.enviarPagoVerificado(
                pago.getInscripcion().getUsuario().getEmail(),
                false,
                pago.getInscripcion().getCurso().getNombre()
            );
        }

        return pagoRepository.save(pago);
    }

    public Optional<Pago> findByInscripcionId(Long inscripcionId) {
        return pagoRepository.findByInscripcionId(inscripcionId);
    }

    public List<Pago> findByEstado(String estado) {
        return pagoRepository.findByEstado(estado);
    }
} 