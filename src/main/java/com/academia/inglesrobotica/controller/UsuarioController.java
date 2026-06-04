package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.model.Usuario;
import com.academia.inglesrobotica.service.UsuarioService;
import com.academia.inglesrobotica.service.RolService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @GetMapping
    public String listar(Model model, HttpSession session,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }

        List<Usuario> todos = usuarioService.findAll();
        int total = todos.size();
        int totalPages = (int) Math.ceil((double) total / size);

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, total);

        List<Usuario> usuarios;
        if (fromIndex < total) {
            usuarios = todos.subList(fromIndex, toIndex);
        } else {
            usuarios = new ArrayList<>();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalUsuarios", total);

        return "admin/usuarios/lista";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || !email.equals("admin@test.com")) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.findAll());
        return "admin/usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario) {
        usuarioService.save(usuario);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return "redirect:/admin/usuarios";
    }
} 