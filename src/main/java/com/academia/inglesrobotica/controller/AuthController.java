package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.dto.LoginRequest;
import com.academia.inglesrobotica.dto.LoginResponse;
import com.academia.inglesrobotica.dto.RegistroRequest;
import com.academia.inglesrobotica.model.Rol;
import com.academia.inglesrobotica.model.Usuario;
import com.academia.inglesrobotica.security.JwtTokenProvider;
import com.academia.inglesrobotica.service.EmailService;
import com.academia.inglesrobotica.service.RolService;
import com.academia.inglesrobotica.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroRequest", new RegistroRequest());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute RegistroRequest request, Model model) {
        if (usuarioService.existsByEmail(request.getEmail())) {
            model.addAttribute("error", "El email ya está registrado");
            return "auth/registro";
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setTelefono(request.getTelefono());
        usuario.setActivo(true);

        Rol rol = rolService.findById(request.getRolId() != null ? request.getRolId() : 3L)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rol);

        usuarioService.save(usuario);
        return "redirect:/auth/login?registro=exitoso";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                                 @RequestParam String password,
                                 Model model,
                                 HttpSession session) {
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);

        if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
            session.setAttribute("usuarioEmail", usuario.getEmail());
            session.setAttribute("usuarioId", usuario.getId());

            String rol = usuario.getRol().getNombre();
            if (rol.contains("ADMIN")) return "redirect:/admin/dashboard";
            if (rol.contains("PROFESOR")) return "redirect:/profesor/dashboard";
            if (rol.contains("PADRE")) return "redirect:/padre/dashboard";
            return "redirect:/alumno/dashboard";
        }

        model.addAttribute("error", "Email o contraseña incorrectos");
        return "auth/login";
    }

    @GetMapping("/recuperar")
    public String mostrarRecuperar() {
        return "auth/recuperar";
    }

    @PostMapping("/recuperar")
    public String procesarRecuperar(@RequestParam String email, Model model) {
        Optional<Usuario> optUsuario = usuarioService.findByEmail(email);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            String token = UUID.randomUUID().toString();
            usuario.setTokenReset(token);
            usuarioService.save(usuario);
            emailService.enviarRecuperacionPassword(usuario.getEmail(), token);
            model.addAttribute("success", "✅ Se ha enviado un enlace a tu correo.");
        } else {
            model.addAttribute("error", "❌ No existe una cuenta con ese correo.");
        }
        return "auth/recuperar";
    }

    @GetMapping("/restablecer")
    public String mostrarRestablecer(@RequestParam String token, Model model) {
        Optional<Usuario> optUsuario = usuarioService.findByTokenReset(token);
        if (optUsuario.isPresent()) {
            model.addAttribute("token", token);
            return "auth/restablecer";
        }
        model.addAttribute("error", "❌ Token inválido o expirado.");
        return "auth/recuperar";
    }

    @PostMapping("/restablecer")
    public String procesarRestablecer(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmar,
                                       Model model) {
        if (!password.equals(confirmar)) {
            model.addAttribute("error", "❌ Las contraseñas no coinciden.");
            model.addAttribute("token", token);
            return "auth/restablecer";
        }

        Optional<Usuario> optUsuario = usuarioService.findByTokenReset(token);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setTokenReset(null);
            usuarioService.save(usuario);
            model.addAttribute("success", "✅ Contraseña actualizada. Inicia sesión.");
            return "auth/login";
        }

        model.addAttribute("error", "❌ Token inválido.");
        return "auth/recuperar";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtTokenProvider.generateToken(usuario.getEmail());

        LoginResponse response = new LoginResponse(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getRol().getNombre()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login?logout";
    }
} 