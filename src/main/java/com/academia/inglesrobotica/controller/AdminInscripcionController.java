package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.model.Inscripcion;
import com.academia.inglesrobotica.model.Pago;
import com.academia.inglesrobotica.service.InscripcionService;
import com.academia.inglesrobotica.service.PagoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/inscripciones")
public class AdminInscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public String listar(Model model, HttpSession session,
                         @RequestParam(defaultValue = "TODAS") String filtro) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }

        List<Inscripcion> inscripciones;
        if ("TODAS".equals(filtro)) {
            inscripciones = inscripcionService.findAll();
        } else {
            inscripciones = inscripcionService.findByEstado(filtro);
        }

        model.addAttribute("inscripciones", inscripciones);
        model.addAttribute("filtroActual", filtro);
        model.addAttribute("pendientesPago", inscripcionService.countByEstado("PENDIENTE_PAGO"));
        model.addAttribute("pagoVerificacion", inscripcionService.countByEstado("PAGO_VERIFICACION"));
        model.addAttribute("activas", inscripcionService.countByEstado("ACTIVA"));

        return "admin/inscripciones/lista";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }

        Inscripcion inscripcion = inscripcionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        Pago pago = pagoService.findByInscripcionId(id).orElse(null);

        model.addAttribute("inscripcion", inscripcion);
        model.addAttribute("pago", pago);
        return "admin/inscripciones/detalle";
    }

    @PostMapping("/verificar-pago/{id}")
    public String verificarPago(@PathVariable Long id,
                                 @RequestParam boolean aprobado,
                                 @RequestParam(required = false) String observaciones,
                                 RedirectAttributes redirectAttributes) {
        try {
            pagoService.verificarPago(id, aprobado, observaciones);
            String mensaje = aprobado ? "✅ Pago verificado correctamente." : "❌ Pago rechazado.";
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error: " + e.getMessage());
        }
        return "redirect:/admin/inscripciones";
    }
} 