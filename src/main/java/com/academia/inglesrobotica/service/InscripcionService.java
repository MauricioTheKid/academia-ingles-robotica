package com.academia.inglesrobotica.service;

import com.academia.inglesrobotica.dto.InscripcionRequest;
import com.academia.inglesrobotica.model.Inscripcion;
import com.academia.inglesrobotica.model.Reserva;
import com.academia.inglesrobotica.model.Usuario;
import com.academia.inglesrobotica.repository.InscripcionRepository;
import com.academia.inglesrobotica.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Inscripcion crearInscripcion(InscripcionRequest request, Usuario usuario) {
        System.out.println("=== DEBUG: Iniciando creación de inscripción en Service ===");
        System.out.println("ReservaId recibido: " + request.getReservaId());
        System.out.println("Usuario ID: " + usuario.getId());
        System.out.println("Usuario Email: " + usuario.getEmail());
        
        if (request.getReservaId() == null) {
            throw new RuntimeException("El ID de reserva no puede ser nulo");
        }
        
        Reserva reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + request.getReservaId()));

        System.out.println("Reserva encontrada - ID: " + reserva.getId() + ", Estado: " + reserva.getEstado());
        System.out.println("Reserva pertenece al usuario ID: " + reserva.getUsuario().getId());

        // Validar que la reserva pertenezca al usuario
        if (!reserva.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Esta reserva no te pertenece. Reserva usuario: " + 
                reserva.getUsuario().getId() + ", Tu ID: " + usuario.getId());
        }

        // Validar que la reserva esté CONFIRMADA
        if (!"CONFIRMADA".equals(reserva.getEstado())) {
            throw new RuntimeException("La reserva debe estar confirmada para inscribirse. Estado actual: " + 
                reserva.getEstado());
        }

        // Validar que no exista una inscripción para esta reserva
        Optional<Inscripcion> existente = inscripcionRepository.findByReservaId(reserva.getId());
        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe una inscripción para esta reserva (ID: " + 
                existente.get().getId() + ")");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setReserva(reserva);
        inscripcion.setUsuario(usuario);
        inscripcion.setCurso(reserva.getHorario().getCurso());
        inscripcion.setFechaNacimiento(request.getFechaNacimiento());
        inscripcion.setGradoEscolar(request.getGradoEscolar());
        inscripcion.setContactoEmergencia(request.getContactoEmergencia());
        inscripcion.setTelefonoEmergencia(request.getTelefonoEmergencia());
        inscripcion.setAlergias(request.getAlergias());
        inscripcion.setCondicionesMedicas(request.getCondicionesMedicas());
        inscripcion.setAutorizacionFoto(request.getAutorizacionFoto() != null ? request.getAutorizacionFoto() : "NO");
        inscripcion.setTallaUniforme(request.getTallaUniforme());
        inscripcion.setEstado("PENDIENTE_PAGO");
        inscripcion.setFechaInscripcion(LocalDateTime.now());

        System.out.println("Guardando inscripción en la base de datos...");
        System.out.println("Datos de inscripción:");
        System.out.println("- Curso: " + inscripcion.getCurso().getNombre());
        System.out.println("- Estado: " + inscripcion.getEstado());
        System.out.println("- Fecha Nacimiento: " + inscripcion.getFechaNacimiento());
        
        Inscripcion saved = inscripcionRepository.save(inscripcion);
        System.out.println("✅ Inscripción guardada exitosamente con ID: " + saved.getId());

        // Enviar email de bienvenida
        try {
            emailService.enviarInicioInscripcion(usuario.getEmail(), saved.getId());
            System.out.println("Email de inicio de inscripción enviado a: " + usuario.getEmail());
        } catch (Exception e) {
            System.out.println("⚠️ Error al enviar email (no crítico): " + e.getMessage());
        }

        return saved;
    }

    public List<Inscripcion> findByUsuarioId(Long usuarioId) {
        return inscripcionRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Inscripcion> findById(Long id) {
        return inscripcionRepository.findById(id);
    }

    public List<Inscripcion> findAll() {
        return inscripcionRepository.findAll();
    }

    public List<Inscripcion> findByEstado(String estado) {
        return inscripcionRepository.findByEstado(estado);
    }

    @Transactional
    public Inscripcion actualizarEstado(Long id, String estado, String observaciones) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        inscripcion.setEstado(estado);
        inscripcion.setObservaciones(observaciones);
        
        if ("ACTIVA".equals(estado)) {
            inscripcion.setFechaPago(LocalDateTime.now());
            try {
                emailService.enviarInscripcionExitosa(
                    inscripcion.getUsuario().getEmail(),
                    inscripcion.getCurso().getNombre(),
                    inscripcion.getId()
                );
            } catch (Exception e) {
                System.out.println("⚠️ Error al enviar email de inscripción exitosa: " + e.getMessage());
            }
        }
        
        return inscripcionRepository.save(inscripcion);
    }

    @Transactional
    public Inscripcion guardarActaNacimiento(Long id, String url) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        inscripcion.setActaNacimientoUrl(url);
        return inscripcionRepository.save(inscripcion);
    }

    @Transactional
    public Inscripcion guardarComprobantePago(Long id, String url) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        inscripcion.setComprobantePagoUrl(url);
        return inscripcionRepository.save(inscripcion);
    }

    public long countByEstado(String estado) {
        return inscripcionRepository.countByEstado(estado);
    }
} 