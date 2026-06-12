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
        Reserva reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Validar que la reserva pertenezca al usuario
        if (!reserva.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Esta reserva no te pertenece");
        }

        // Validar que la reserva esté CONFIRMADA
        if (!"CONFIRMADA".equals(reserva.getEstado())) {
            throw new RuntimeException("La reserva debe estar confirmada para inscribirse");
        }

        // Validar que no exista una inscripción para esta reserva
        Optional<Inscripcion> existente = inscripcionRepository.findByReservaId(reserva.getId());
        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe una inscripción para esta reserva");
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

        Inscripcion saved = inscripcionRepository.save(inscripcion);

        // Enviar email de bienvenida al proceso de inscripción
        emailService.enviarInicioInscripcion(usuario.getEmail(), saved.getId());

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
            // Enviar email de inscripción exitosa
            emailService.enviarInscripcionExitosa(
                inscripcion.getUsuario().getEmail(),
                inscripcion.getCurso().getNombre(),
                inscripcion.getId()
            );
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