package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.dto.InscripcionRequest;
import com.academia.inglesrobotica.dto.PagoRequest;
import com.academia.inglesrobotica.model.Inscripcion;
import com.academia.inglesrobotica.model.Pago;
import com.academia.inglesrobotica.model.Reserva;
import com.academia.inglesrobotica.model.Usuario;
import com.academia.inglesrobotica.service.InscripcionService;
import com.academia.inglesrobotica.service.PagoService;
import com.academia.inglesrobotica.service.ReservaService;
import com.academia.inglesrobotica.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/inscripcion")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PagoService pagoService;

    private static final String ACTAS_DIR = "src/main/resources/static/uploads/actas/";
    private static final String COMPROBANTES_DIR = "src/main/resources/static/uploads/comprobantes/";

    @GetMapping("/iniciar/{reservaId}")
    public String iniciarInscripcion(@PathVariable Long reservaId, HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Reserva reserva = reservaService.findById(reservaId).orElse(null);
        if (reserva == null || !reserva.getUsuario().getId().equals(usuarioId)) {
            return "redirect:/reservas/mis-reservas";
        }

        if (!"CONFIRMADA".equals(reserva.getEstado())) {
            model.addAttribute("error", "Esta reserva aún no está confirmada. Espera la confirmación del administrador.");
            return "redirect:/reservas/mis-reservas";
        }

        model.addAttribute("reserva", reserva);
        model.addAttribute("inscripcionRequest", new InscripcionRequest());
        model.addAttribute("reservaId", reservaId);
        return "inscripcion/formulario";
    }

    @PostMapping("/guardar")
    public String guardarInscripcion(@Valid @ModelAttribute InscripcionRequest request,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Usuario usuario = usuarioService.findById(usuarioId).orElse(null);
        request.setReservaId(request.getReservaId());

        try {
            Inscripcion inscripcion = inscripcionService.crearInscripcion(request, usuario);
            redirectAttributes.addFlashAttribute("success", "✅ Datos guardados. Ahora sube tu acta de nacimiento.");
            return "redirect:/inscripcion/documentos/" + inscripcion.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error: " + e.getMessage());
            return "redirect:/inscripcion/iniciar/" + request.getReservaId();
        }
    }

    @GetMapping("/documentos/{id}")
    public String mostrarDocumentos(@PathVariable Long id, HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Inscripcion inscripcion = inscripcionService.findById(id).orElse(null);
        if (inscripcion == null || !inscripcion.getUsuario().getId().equals(usuarioId)) {
            return "redirect:/inscripcion/mis-inscripciones";
        }

        model.addAttribute("inscripcion", inscripcion);
        return "inscripcion/documentos";
    }

    @PostMapping("/subir-acta/{id}")
    public String subirActa(@PathVariable Long id,
                             @RequestParam("archivo") MultipartFile archivo,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        try {
            if (archivo.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "❌ Selecciona un archivo.");
                return "redirect:/inscripcion/documentos/" + id;
            }

            Path dir = Paths.get(ACTAS_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            String nombreArchivo = "acta_" + id + "_" + System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
            Path path = Paths.get(ACTAS_DIR + nombreArchivo);
            Files.write(path, archivo.getBytes());

            inscripcionService.guardarActaNacimiento(id, "/uploads/actas/" + nombreArchivo);
            redirectAttributes.addFlashAttribute("success", "✅ Acta de nacimiento subida correctamente.");

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error al subir el archivo.");
        }

        return "redirect:/inscripcion/documentos/" + id;
    }

    @GetMapping("/pago/{id}")
    public String mostrarPago(@PathVariable Long id, HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Inscripcion inscripcion = inscripcionService.findById(id).orElse(null);
        if (inscripcion == null || !inscripcion.getUsuario().getId().equals(usuarioId)) {
            return "redirect:/inscripcion/mis-inscripciones";
        }

        model.addAttribute("inscripcion", inscripcion);
        model.addAttribute("pagoRequest", new PagoRequest());
        return "inscripcion/pago";
    }

    @PostMapping("/registrar-pago")
    public String registrarPago(@Valid @ModelAttribute PagoRequest request,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        try {
            Pago pago = pagoService.registrarPago(request);
            redirectAttributes.addFlashAttribute("success", "✅ Pago registrado. Ahora sube tu comprobante.");
            return "redirect:/inscripcion/subir-comprobante/" + pago.getInscripcion().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error: " + e.getMessage());
            return "redirect:/inscripcion/pago/" + request.getInscripcionId();
        }
    }

    @GetMapping("/subir-comprobante/{id}")
    public String mostrarSubirComprobante(@PathVariable Long id, HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Inscripcion inscripcion = inscripcionService.findById(id).orElse(null);
        if (inscripcion == null || !inscripcion.getUsuario().getId().equals(usuarioId)) {
            return "redirect:/inscripcion/mis-inscripciones";
        }

        model.addAttribute("inscripcion", inscripcion);
        return "inscripcion/subir-comprobante";
    }

    @PostMapping("/subir-comprobante/{id}")
    public String subirComprobante(@PathVariable Long id,
                                    @RequestParam("archivo") MultipartFile archivo,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        try {
            if (archivo.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "❌ Selecciona un comprobante.");
                return "redirect:/inscripcion/subir-comprobante/" + id;
            }

            Path dir = Paths.get(COMPROBANTES_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            String nombreArchivo = "comprobante_" + id + "_" + System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
            Path path = Paths.get(COMPROBANTES_DIR + nombreArchivo);
            Files.write(path, archivo.getBytes());

            inscripcionService.guardarComprobantePago(id, "/uploads/comprobantes/" + nombreArchivo);
            redirectAttributes.addFlashAttribute("success", "✅ Comprobante subido. Espera la verificación del administrador.");
            return "redirect:/inscripcion/exito/" + id;

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error al subir el comprobante.");
            return "redirect:/inscripcion/subir-comprobante/" + id;
        }
    }

    @GetMapping("/exito/{id}")
    public String exito(@PathVariable Long id, HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Inscripcion inscripcion = inscripcionService.findById(id).orElse(null);
        model.addAttribute("inscripcion", inscripcion);
        return "inscripcion/exito";
    }

    @GetMapping("/mis-inscripciones")
    public String misInscripciones(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        List<Inscripcion> inscripciones = inscripcionService.findByUsuarioId(usuarioId);
        model.addAttribute("inscripciones", inscripciones);
        return "inscripcion/mis-inscripciones";
    }

    // ==================== NUEVO MÉTODO PARA DETALLE DE INSCRIPCIÓN ====================
    @GetMapping("/detalle/{id}")
    public String detalleInscripcion(@PathVariable Long id, HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Inscripcion inscripcion = inscripcionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        
        // Verificar que la inscripción pertenezca al usuario
        if (!inscripcion.getUsuario().getId().equals(usuarioId)) {
            return "redirect:/inscripcion/mis-inscripciones";
        }
        
        Pago pago = pagoService.findByInscripcionId(id).orElse(null);
        
        model.addAttribute("inscripcion", inscripcion);
        model.addAttribute("pago", pago);
        return "inscripcion/detalle";
    }
} 