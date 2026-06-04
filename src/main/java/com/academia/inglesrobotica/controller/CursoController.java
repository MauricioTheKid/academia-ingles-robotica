package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.model.Curso;
import com.academia.inglesrobotica.model.Horario;
import com.academia.inglesrobotica.model.Nivel;
import com.academia.inglesrobotica.model.Reserva;
import com.academia.inglesrobotica.service.CursoService;
import com.academia.inglesrobotica.service.ReservaService;
import com.academia.inglesrobotica.repository.NivelRepository;
import com.academia.inglesrobotica.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private NivelRepository nivelRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public String listar(Model model) {
        List<Curso> cursos = cursoService.findAll();
        model.addAttribute("cursos", cursos);
        return "publico/cursos";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        List<Nivel> niveles = nivelRepository.findAll();
        model.addAttribute("curso", new Curso());
        model.addAttribute("niveles", niveles);
        return "admin/cursos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Curso curso = cursoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        List<Nivel> niveles = nivelRepository.findAll();
        model.addAttribute("curso", curso);
        model.addAttribute("niveles", niveles);
        return "admin/cursos/formulario";
    }

    @GetMapping("/horarios/{id}")
    public String verHorarios(@PathVariable Long id, Model model) {
        Curso curso = cursoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        List<Horario> horarios = horarioRepository.findByCursoId(id);
        model.addAttribute("curso", curso);
        model.addAttribute("horarios", horarios);
        return "cursos/horarios";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Curso curso = cursoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        model.addAttribute("curso", curso);
        return "publico/cursos";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Curso curso) {
        curso.setActivo(true);
        cursoService.save(curso);
        return "redirect:/cursos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        // Eliminar reservas y horarios asociados primero
        List<Horario> horarios = horarioRepository.findByCursoId(id);
        for (Horario h : horarios) {
            List<Reserva> reservas = reservaService.findByHorarioId(h.getId());
            for (Reserva r : reservas) {
                reservaService.deleteById(r.getId());
            }
            horarioRepository.deleteById(h.getId());
        }
        cursoService.deleteById(id);
        return "redirect:/cursos";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public List<Curso> listarApi() {
        return cursoService.findActivos();
    }
} 