package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.model.Usuario;
import com.academia.inglesrobotica.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String verPerfil(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.findById(usuarioId).orElse(null);
        model.addAttribute("usuario", usuario);
        return "perfil/ver";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioForm,
                                    HttpSession session,
                                    Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.findById(usuarioId).orElse(null);
        if (usuario != null) {
            usuario.setNombre(usuarioForm.getNombre());
            usuario.setApellido(usuarioForm.getApellido());
            usuario.setTelefono(usuarioForm.getTelefono());
            usuarioService.save(usuario);
            model.addAttribute("success", "Perfil actualizado correctamente.");
        }
        model.addAttribute("usuario", usuario);
        return "perfil/ver";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                   @RequestParam String passwordNueva,
                                   @RequestParam String passwordConfirmar,
                                   HttpSession session,
                                   Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.findById(usuarioId).orElse(null);
        
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            model.addAttribute("error", "La contraseña actual es incorrecta.");
        } else if (!passwordNueva.equals(passwordConfirmar)) {
            model.addAttribute("error", "Las contraseñas nuevas no coinciden.");
        } else if (passwordNueva.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
        } else {
            usuario.setPassword(passwordEncoder.encode(passwordNueva));
            usuarioService.save(usuario);
            model.addAttribute("success", "Contraseña cambiada correctamente.");
        }

        model.addAttribute("usuario", usuario);
        return "perfil/ver";
    }
} 