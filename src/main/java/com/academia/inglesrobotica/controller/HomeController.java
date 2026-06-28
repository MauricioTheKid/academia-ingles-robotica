package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.model.Inscripcion;
import com.academia.inglesrobotica.model.Pago;
import com.academia.inglesrobotica.model.Reserva;
import com.academia.inglesrobotica.service.CursoService;
import com.academia.inglesrobotica.service.InscripcionService;
import com.academia.inglesrobotica.service.PagoService;
import com.academia.inglesrobotica.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private CursoService cursoService;
    
    @Autowired
    private InscripcionService inscripcionService;
    
    @Autowired
    private PagoService pagoService;

    @GetMapping("/")
    public String index() {
        return "publico/index";
    }

    @GetMapping("/publico/cursos")
    public String cursosPublicos() {
        return "publico/cursos";
    }

    @GetMapping("/publico/contacto")
    public String contacto() {
        return "publico/contacto";
    }

    @GetMapping("/alumno/dashboard")
    public String alumnoDashboard(Model model, HttpSession session) {
        model.addAttribute("totalCursos", cursoService.findActivos().size());

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        List<Reserva> reservas = new ArrayList<>();
        if (usuarioId != null) {
            reservas = reservaService.findByUsuarioId(usuarioId);
        }

        long pendientes = 0;
        long confirmadas = 0;
        for (Reserva r : reservas) {
            if ("PENDIENTE".equals(r.getEstado())) pendientes++;
            if ("CONFIRMADA".equals(r.getEstado())) confirmadas++;
        }

        model.addAttribute("totalReservas", reservas.size());
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("confirmadas", confirmadas);
        model.addAttribute("pendientesAttr", pendientes);
        model.addAttribute("confirmadasAttr", confirmadas);
        model.addAttribute("reservas", reservas);
        return "alumno/dashboard";
    }

    @GetMapping("/padre/dashboard")
    public String padreDashboard(Model model, HttpSession session) {
        model.addAttribute("totalCursos", cursoService.findActivos().size());

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        List<Reserva> reservas = new ArrayList<>();
        if (usuarioId != null) {
            reservas = reservaService.findByUsuarioId(usuarioId);
        }

        long pendientes = 0;
        long confirmadas = 0;
        for (Reserva r : reservas) {
            if ("PENDIENTE".equals(r.getEstado())) pendientes++;
            if ("CONFIRMADA".equals(r.getEstado())) confirmadas++;
        }

        model.addAttribute("totalReservas", reservas.size());
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("confirmadas", confirmadas);
        model.addAttribute("pendientesAttr", pendientes);
        model.addAttribute("confirmadasAttr", confirmadas);
        model.addAttribute("reservas", reservas);
        return "padre/dashboard";
    }

    @GetMapping("/profesor/dashboard")
    public String profesorDashboard(Model model, HttpSession session) {
        model.addAttribute("totalCursos", cursoService.findActivos().size());

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        List<Reserva> reservas = new ArrayList<>();
        if (usuarioId != null) {
            reservas = reservaService.findByUsuarioId(usuarioId);
        }

        long pendientes = 0;
        long confirmadas = 0;
        for (Reserva r : reservas) {
            if ("PENDIENTE".equals(r.getEstado())) pendientes++;
            if ("CONFIRMADA".equals(r.getEstado())) confirmadas++;
        }

        model.addAttribute("totalReservas", reservas.size());
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("confirmadas", confirmadas);
        model.addAttribute("pendientesAttr", pendientes);
        model.addAttribute("confirmadasAttr", confirmadas);
        model.addAttribute("reservas", reservas);

        return "profesor/dashboard";
    }

    @GetMapping("/profesor/alumnos")
    public String profesorAlumnos(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }
        
        List<Inscripcion> inscripcionesActivas = inscripcionService.findByEstado("ACTIVA");
        List<Inscripcion> inscripcionesPendientes = inscripcionService.findByEstado("PENDIENTE_PAGO");
        List<Inscripcion> inscripcionesVerificacion = inscripcionService.findByEstado("PAGO_VERIFICACION");
        
        model.addAttribute("inscripcionesActivas", inscripcionesActivas);
        model.addAttribute("inscripcionesPendientes", inscripcionesPendientes);
        model.addAttribute("inscripcionesVerificacion", inscripcionesVerificacion);
        model.addAttribute("totalAlumnos", inscripcionesActivas.size());
        
        return "profesor/alumnos";
    }
    
    @GetMapping("/profesor/alumno/{id}")
    public String verAlumno(@PathVariable Long id, HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }
        
        Inscripcion inscripcion = inscripcionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        
        Pago pago = pagoService.findByInscripcionId(id).orElse(null);
        
        model.addAttribute("inscripcion", inscripcion);
        model.addAttribute("pago", pago);
        
        return "profesor/ver-alumno";
    }
} 