package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.model.Horario;
import com.academia.inglesrobotica.model.Curso;
import com.academia.inglesrobotica.repository.HorarioRepository;
import com.academia.inglesrobotica.repository.CursoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/horarios")
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping
    public String listar(Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }
        List<Horario> horarios = horarioRepository.findAll();
        model.addAttribute("horarios", horarios);
        return "admin/horarios/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }
        List<Curso> cursos = cursoRepository.findAll();
        model.addAttribute("horario", new Horario());
        model.addAttribute("cursos", cursos);
        return "admin/horarios/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        List<Curso> cursos = cursoRepository.findAll();
        model.addAttribute("horario", horario);
        model.addAttribute("cursos", cursos);
        return "admin/horarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Horario horario) {
        horarioRepository.save(horario);
        return "redirect:/horarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        horarioRepository.deleteById(id);
        return "redirect:/horarios";
    }
}