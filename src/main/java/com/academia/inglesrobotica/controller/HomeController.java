package com.academia.inglesrobotica.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.academia.inglesrobotica.model.Calificacion;
import com.academia.inglesrobotica.model.Curso;
import com.academia.inglesrobotica.model.Horario;
import com.academia.inglesrobotica.model.Inscripcion;
import com.academia.inglesrobotica.model.Pago;
import com.academia.inglesrobotica.model.Reserva;
import com.academia.inglesrobotica.model.Usuario;
import com.academia.inglesrobotica.repository.HorarioRepository;
import com.academia.inglesrobotica.service.CalificacionService;
import com.academia.inglesrobotica.service.CursoService;
import com.academia.inglesrobotica.service.InscripcionService;
import com.academia.inglesrobotica.service.PagoService;
import com.academia.inglesrobotica.service.ProfesorCursoService;
import com.academia.inglesrobotica.service.ReservaService;
import com.academia.inglesrobotica.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

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

    @Autowired
    private CalificacionService calificacionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProfesorCursoService profesorCursoService;

    @Autowired
    private HorarioRepository horarioRepository;

    @GetMapping("/")
    public String index() {
        return "publico/index";
    }

    @GetMapping("/publico/cursos")
    public String cursosPublicos(Model model) {
        model.addAttribute("cursos", cursoService.findActivos());
        return "publico/cursos";
    }

    @GetMapping("/publico/contacto")
    public String contacto() {
        return "publico/contacto";
    }

    // ==================== ALUMNO ====================

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
            if ("PENDIENTE".equals(r.getEstado()))
                pendientes++;
            if ("CONFIRMADA".equals(r.getEstado()))
                confirmadas++;
        }

        model.addAttribute("totalReservas", reservas.size());
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("confirmadas", confirmadas);
        model.addAttribute("pendientesAttr", pendientes);
        model.addAttribute("confirmadasAttr", confirmadas);
        model.addAttribute("reservas", reservas);
        return "alumno/dashboard";
    }

    @GetMapping("/alumno/calificaciones")
    public String misCalificaciones(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        List<Calificacion> calificaciones = calificacionService.findByUsuarioId(usuarioId);
        model.addAttribute("calificaciones", calificaciones);

        return "alumno/calificaciones";
    }

    // ==================== CLASE VIRTUAL ====================

    @GetMapping("/alumno/clase-virtual/{horarioId}")
    public String claseVirtual(@PathVariable Long horarioId, HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        model.addAttribute("horario", horario);
        return "alumno/clase-virtual";
    }

    // ==================== PADRE ====================

    @GetMapping("/padre/dashboard")
    public String padreDashboard(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Usuario padre = usuarioService.findById(usuarioId).orElse(null);
        if (padre == null) {
            return "redirect:/auth/login";
        }

        List<Usuario> hijos = usuarioService.findHijosByParentId(usuarioId);

        List<Inscripcion> todasInscripciones = new ArrayList<>();
        for (Usuario hijo : hijos) {
            List<Inscripcion> ins = inscripcionService.findByUsuarioId(hijo.getId());
            todasInscripciones.addAll(ins);
        }

        long activas = todasInscripciones.stream().filter(i -> "ACTIVA".equals(i.getEstado())).count();
        long pendientes = todasInscripciones.stream().filter(i -> "PENDIENTE_PAGO".equals(i.getEstado())).count();
        long verificacion = todasInscripciones.stream().filter(i -> "PAGO_VERIFICACION".equals(i.getEstado())).count();

        Map<Long, Calificacion> calificacionesHijos = new HashMap<>();
        for (Inscripcion ins : todasInscripciones) {
            calificacionService.findByInscripcionId(ins.getId())
                    .ifPresent(cal -> calificacionesHijos.put(ins.getId(), cal));
        }

        long pagosCompletados = 0;
        BigDecimal totalInvertido = BigDecimal.ZERO;
        for (Inscripcion ins : todasInscripciones) {
            Optional<Pago> pagoOpt = pagoService.findByInscripcionId(ins.getId());
            if (pagoOpt.isPresent() && "COMPLETADO".equals(pagoOpt.get().getEstado())) {
                pagosCompletados++;
                if (pagoOpt.get().getMonto() != null) {
                    totalInvertido = totalInvertido.add(pagoOpt.get().getMonto());
                }
            }
        }

        BigDecimal promedioGeneral = BigDecimal.ZERO;
        int countPromedios = 0;
        for (Calificacion cal : calificacionesHijos.values()) {
            if (cal.getPromedio().compareTo(BigDecimal.ZERO) > 0) {
                promedioGeneral = promedioGeneral.add(cal.getPromedio());
                countPromedios++;
            }
        }
        if (countPromedios > 0) {
            promedioGeneral = promedioGeneral.divide(BigDecimal.valueOf(countPromedios), 1, RoundingMode.HALF_UP);
        }

        model.addAttribute("hijos", hijos);
        model.addAttribute("inscripciones", todasInscripciones);
        model.addAttribute("inscripcionesActivas", activas);
        model.addAttribute("inscripcionesPendientes", pendientes);
        model.addAttribute("inscripcionesVerificacion", verificacion);
        model.addAttribute("calificacionesHijos", calificacionesHijos);
        model.addAttribute("pagosCompletados", pagosCompletados);
        model.addAttribute("totalInvertido", totalInvertido);
        model.addAttribute("promedioGeneral", promedioGeneral);
        model.addAttribute("totalHijos", hijos.size());

        return "padre/dashboard";
    }

    // ==================== VER DETALLE DE UN HIJO ====================

    @GetMapping("/padre/ver-hijo/{id}")
    public String verHijo(@PathVariable Long id, HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Usuario padre = usuarioService.findById(usuarioId).orElse(null);
        if (padre == null || !padre.getRol().getNombre().contains("PADRE")) {
            return "redirect:/auth/login";
        }

        Usuario hijo = usuarioService.findById(id).orElse(null);
        if (hijo == null || hijo.getParent() == null || !hijo.getParent().getId().equals(usuarioId)) {
            return "redirect:/padre/dashboard";
        }

        List<Inscripcion> inscripciones = inscripcionService.findByUsuarioId(id);

        Map<Long, Calificacion> calificacionesHijos = new HashMap<>();
        for (Inscripcion ins : inscripciones) {
            calificacionService.findByInscripcionId(ins.getId())
                    .ifPresent(cal -> calificacionesHijos.put(ins.getId(), cal));
        }

        model.addAttribute("hijo", hijo);
        model.addAttribute("inscripciones", inscripciones);
        model.addAttribute("calificacionesHijos", calificacionesHijos);

        return "padre/ver-hijo";
    }

    // ==================== PROFESOR ====================

    @GetMapping("/profesor/dashboard")
    public String profesorDashboard(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        List<Curso> cursosAsignados = profesorCursoService.findCursosByProfesorId(usuarioId);
        List<Long> cursoIds = profesorCursoService.findCursoIdsByProfesorId(usuarioId);

        model.addAttribute("totalCursos", cursosAsignados.size());
        model.addAttribute("cursosAsignados", cursosAsignados);

        List<Inscripcion> inscripcionesActivas = inscripcionService.findByEstado("ACTIVA");
        List<Inscripcion> misAlumnos = new ArrayList<>();

        for (Inscripcion ins : inscripcionesActivas) {
            if (cursoIds.contains(ins.getCurso().getId())) {
                misAlumnos.add(ins);
            }
        }

        model.addAttribute("inscripcionesActivas", misAlumnos);
        model.addAttribute("totalAlumnosActivos", misAlumnos.size());

        Map<Long, Calificacion> mapaCalificaciones = new HashMap<>();
        long calificacionesPendientes = 0;

        for (Inscripcion ins : misAlumnos) {
            Optional<Calificacion> calOpt = calificacionService.findByInscripcionId(ins.getId());
            if (calOpt.isPresent()) {
                mapaCalificaciones.put(ins.getId(), calOpt.get());
            } else {
                calificacionesPendientes++;
            }
        }

        model.addAttribute("mapaCalificaciones", mapaCalificaciones);
        model.addAttribute("calificacionesPendientes", calificacionesPendientes);

        Map<String, List<Inscripcion>> alumnosPorCurso = new HashMap<>();
        for (Inscripcion ins : misAlumnos) {
            String cursoNombre = ins.getCurso().getNombre();
            alumnosPorCurso.computeIfAbsent(cursoNombre, k -> new ArrayList<>()).add(ins);
        }
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);

        return "profesor/dashboard";
    }

    @GetMapping("/profesor/alumnos")
    public String profesorAlumnos(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        List<Long> cursoIds = profesorCursoService.findCursoIdsByProfesorId(usuarioId);

        List<Inscripcion> inscripcionesActivas = inscripcionService.findByEstado("ACTIVA");
        List<Inscripcion> inscripcionesPendientes = inscripcionService.findByEstado("PENDIENTE_PAGO");
        List<Inscripcion> inscripcionesVerificacion = inscripcionService.findByEstado("PAGO_VERIFICACION");

        List<Inscripcion> misActivas = new ArrayList<>();
        List<Inscripcion> misPendientes = new ArrayList<>();
        List<Inscripcion> misVerificacion = new ArrayList<>();

        for (Inscripcion ins : inscripcionesActivas) {
            if (cursoIds.contains(ins.getCurso().getId()))
                misActivas.add(ins);
        }
        for (Inscripcion ins : inscripcionesPendientes) {
            if (cursoIds.contains(ins.getCurso().getId()))
                misPendientes.add(ins);
        }
        for (Inscripcion ins : inscripcionesVerificacion) {
            if (cursoIds.contains(ins.getCurso().getId()))
                misVerificacion.add(ins);
        }

        model.addAttribute("inscripcionesActivas", misActivas);
        model.addAttribute("inscripcionesPendientes", misPendientes);
        model.addAttribute("inscripcionesVerificacion", misVerificacion);
        model.addAttribute("totalAlumnos", misActivas.size());

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

    // ==================== CALIFICACIONES PROFESOR ====================

    @GetMapping("/profesor/calificaciones")
    public String calificacionesProfesor(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        List<Long> cursoIds = profesorCursoService.findCursoIdsByProfesorId(usuarioId);

        List<Inscripcion> inscripcionesActivas = inscripcionService.findByEstado("ACTIVA");
        List<Inscripcion> misAlumnos = new ArrayList<>();

        for (Inscripcion ins : inscripcionesActivas) {
            if (cursoIds.contains(ins.getCurso().getId())) {
                misAlumnos.add(ins);
            }
        }

        Map<Long, Calificacion> mapaCalificaciones = new HashMap<>();
        for (Inscripcion ins : misAlumnos) {
            calificacionService.findByInscripcionId(ins.getId())
                    .ifPresent(cal -> mapaCalificaciones.put(ins.getId(), cal));
        }

        Map<String, List<Inscripcion>> alumnosPorCurso = new HashMap<>();
        for (Inscripcion ins : misAlumnos) {
            String cursoNombre = ins.getCurso().getNombre();
            alumnosPorCurso.computeIfAbsent(cursoNombre, k -> new ArrayList<>()).add(ins);
        }

        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("mapaCalificaciones", mapaCalificaciones);
        model.addAttribute("cursos", profesorCursoService.findCursosByProfesorId(usuarioId));

        return "profesor/calificaciones";
    }

    @GetMapping("/profesor/calificar/{inscripcionId}")
    public String formularioCalificar(@PathVariable Long inscripcionId,
            Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Inscripcion inscripcion = inscripcionService.findById(inscripcionId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada con ID: " + inscripcionId));

        Calificacion calificacion = calificacionService.findByInscripcionId(inscripcionId)
                .orElse(new Calificacion());

        if (calificacion.getInscripcion() == null) {
            calificacion.setInscripcion(inscripcion);
        }

        model.addAttribute("inscripcion", inscripcion);
        model.addAttribute("calificacion", calificacion);

        return "profesor/calificar";
    }

    @PostMapping("/profesor/calificar/{inscripcionId}")
    public String guardarCalificacion(@PathVariable Long inscripcionId,
            @RequestParam(required = false) String notaParcial1,
            @RequestParam(required = false) String notaParcial2,
            @RequestParam(required = false) String notaParcial3,
            @RequestParam(required = false) String notaParcial4,
            @RequestParam(required = false) String notaProyecto,
            @RequestParam(required = false) String notaExamenFinal,
            @RequestParam(required = false) String observaciones,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        try {
            BigDecimal p1 = convertirANota(notaParcial1);
            BigDecimal p2 = convertirANota(notaParcial2);
            BigDecimal p3 = convertirANota(notaParcial3);
            BigDecimal p4 = convertirANota(notaParcial4);
            BigDecimal proyecto = convertirANota(notaProyecto);
            BigDecimal examenFinal = convertirANota(notaExamenFinal);

            Calificacion calificacion = new Calificacion();
            calificacion.setNotaParcial1(p1);
            calificacion.setNotaParcial2(p2);
            calificacion.setNotaParcial3(p3);
            calificacion.setNotaParcial4(p4);
            calificacion.setNotaProyecto(proyecto);
            calificacion.setNotaExamenFinal(examenFinal);
            calificacion.setObservaciones(observaciones);

            Calificacion saved = calificacionService.guardarOActualizar(inscripcionId, calificacion);

            redirectAttributes.addFlashAttribute("success",
                    "✅ Calificaciones guardadas exitosamente. Promedio: " + saved.getPromedio() + " - Estado: "
                            + saved.getEstado());

            return "redirect:/profesor/calificaciones";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error al guardar: " + e.getMessage());
            return "redirect:/profesor/calificar/" + inscripcionId;
        }
    }

    private BigDecimal convertirANota(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            valor = valor.replace(",", ".").trim();
            BigDecimal nota = new BigDecimal(valor);
            if (nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(new BigDecimal("10")) > 0) {
                return null;
            }
            return nota;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}