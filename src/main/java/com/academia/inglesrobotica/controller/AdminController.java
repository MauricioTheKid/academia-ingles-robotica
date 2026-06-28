package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.model.Calificacion;
import com.academia.inglesrobotica.model.Reserva;
import com.academia.inglesrobotica.service.CalificacionService;
import com.academia.inglesrobotica.service.CursoService;
import com.academia.inglesrobotica.service.ReservaService;
import com.academia.inglesrobotica.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private ReservaService reservaService;
    
    @Autowired
    private CalificacionService calificacionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }

        model.addAttribute("totalUsuarios", usuarioService.findAll().size());
        model.addAttribute("totalCursos", cursoService.findActivos().size());
        model.addAttribute("totalReservas", reservaService.count());

        long pendientes = reservaService.findByEstado("PENDIENTE").size();
        long confirmadas = reservaService.findByEstado("CONFIRMADA").size();
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("confirmadas", confirmadas);

        List<Reserva> ultimasReservas = reservaService.findAll();
        if (ultimasReservas.size() > 5) {
            ultimasReservas = ultimasReservas.subList(ultimasReservas.size() - 5, ultimasReservas.size());
        }
        model.addAttribute("ultimasReservas", ultimasReservas);

        model.addAttribute("pendientesAttr", pendientes);
        model.addAttribute("confirmadasAttr", confirmadas);
        model.addAttribute("usuariosAttr", usuarioService.findAll().size());
        model.addAttribute("cursosAttr", cursoService.findActivos().size());
        model.addAttribute("reservasAttr", reservaService.count());

        return "admin/dashboard";
    }

    @GetMapping("/confirmar-reserva/{id}")
    public String confirmarReserva(@PathVariable Long id, HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }
        Reserva reserva = reservaService.findById(id).orElse(null);
        if (reserva != null) {
            reserva.setEstado("CONFIRMADA");
            reservaService.save(reserva);
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/cancelar-reserva/{id}")
    public String cancelarReserva(@PathVariable Long id, HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }
        Reserva reserva = reservaService.findById(id).orElse(null);
        if (reserva != null) {
            reserva.setEstado("CANCELADA");
            reservaService.save(reserva);
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/reportes")
    public String reportes(Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }

        model.addAttribute("totalUsuarios", usuarioService.findAll().size());
        model.addAttribute("totalCursos", cursoService.findActivos().size());
        model.addAttribute("totalReservas", reservaService.count());
        model.addAttribute("pendientes", reservaService.findByEstado("PENDIENTE").size());
        model.addAttribute("confirmadas", reservaService.findByEstado("CONFIRMADA").size());

        List<Reserva> ultimas = reservaService.findAll();
        if (ultimas.size() > 5) {
            ultimas = ultimas.subList(ultimas.size() - 5, ultimas.size());
        }
        model.addAttribute("ultimasReservas", ultimas);
        model.addAttribute("cursosPopulares", cursoService.findAll());

        return "admin/reportes";
    }

    // ==================== CALIFICACIONES ====================

    @GetMapping("/calificaciones")
    public String calificacionesAdmin(Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }

        List<Calificacion> todas = calificacionService.findAll();
        model.addAttribute("calificaciones", todas);
        model.addAttribute("totalAprobados", calificacionService.countAprobados());
        model.addAttribute("totalReprobados", calificacionService.countReprobados());

        return "admin/calificaciones";
    }
} 