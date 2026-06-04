package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.dto.ReservaRequest;
import com.academia.inglesrobotica.model.Reserva;
import com.academia.inglesrobotica.model.Horario;
import com.academia.inglesrobotica.model.Usuario;
import com.academia.inglesrobotica.security.JwtTokenProvider;
import com.academia.inglesrobotica.service.EmailService;
import com.academia.inglesrobotica.service.ReservaService;
import com.academia.inglesrobotica.repository.HorarioRepository;
import com.academia.inglesrobotica.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String misReservas(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        List<Reserva> reservas;

        if (usuarioId != null) {
            reservas = reservaService.findByUsuarioId(usuarioId);
        } else {
            reservas = reservaService.findAll();
        }

        model.addAttribute("reservas", reservas);
        return "reservas/mis-reservas";
    }

    @GetMapping("/crear/{horarioId}")
    public String crearReservaWeb(@PathVariable Long horarioId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Usuario usuario = usuarioService.findById(usuarioId).orElse(null);
        Horario horario = horarioRepository.findById(horarioId).orElse(null);

        if (usuario != null && horario != null) {
            Reserva reserva = new Reserva();
            reserva.setUsuario(usuario);
            reserva.setHorario(horario);
            reserva.setEstado("PENDIENTE");
            reservaService.save(reserva);
            redirectAttributes.addFlashAttribute("success", "✅ ¡Reserva creada exitosamente!");

            try {
                emailService.enviarConfirmacionReserva(
                    usuario.getEmail(),
                    horario.getCurso().getNombre(),
                    horario.getDiaSemana(),
                    horario.getHoraInicio() + " - " + horario.getHoraFin()
                );
            } catch (Exception e) {
                System.out.println("No se pudo enviar el email: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "❌ No se pudo crear la reserva.");
        }

        return "redirect:/reservas";
    }

    @PostMapping("/api/crear")
    @ResponseBody
    public ResponseEntity<?> crearReserva(@RequestBody ReservaRequest request,
                                           HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        token = token.substring(7);
        String email = jwtTokenProvider.getEmailFromToken(token);
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Horario horario = horarioRepository.findById(request.getHorarioId())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setHorario(horario);
        reserva.setEstado("PENDIENTE");

        reservaService.save(reserva);
        return ResponseEntity.ok("Reserva creada exitosamente");
    }

    @GetMapping("/api/mis-reservas")
    @ResponseBody
    public List<Reserva> misReservasApi(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = jwtTokenProvider.getEmailFromToken(token);
            Usuario usuario = usuarioService.findByEmail(email).orElse(null);
            if (usuario != null) {
                return reservaService.findByUsuarioId(usuario.getId());
            }
        }
        return reservaService.findAll();
    }
} 