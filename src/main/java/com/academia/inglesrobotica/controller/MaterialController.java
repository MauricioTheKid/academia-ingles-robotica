package com.academia.inglesrobotica.controller;

import com.academia.inglesrobotica.model.Material;
import com.academia.inglesrobotica.model.Curso;
import com.academia.inglesrobotica.model.Usuario;
import com.academia.inglesrobotica.repository.MaterialRepository;
import com.academia.inglesrobotica.repository.CursoRepository;
import com.academia.inglesrobotica.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
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
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/materiales")
public class MaterialController {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioService usuarioService;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping
    public String listar(Model model, HttpSession session) {
        List<Material> materiales = materialRepository.findAll();
        model.addAttribute("materiales", materiales);
        model.addAttribute("cursos", cursoRepository.findAll());
        return "materiales/lista";
    }

    @PostMapping("/subir")
    public String subir(@RequestParam String titulo,
                        @RequestParam String descripcion,
                        @RequestParam Long cursoId,
                        @RequestParam("archivo") MultipartFile archivo,
                        @RequestParam(required = false) String enlace,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/auth/login";

        Usuario usuario = usuarioService.findById(usuarioId).orElse(null);
        Curso curso = cursoRepository.findById(cursoId).orElse(null);

        if (usuario != null && curso != null) {
            Material material = new Material();
            material.setTitulo(titulo);
            material.setDescripcion(descripcion);
            material.setCurso(curso);
            material.setUsuario(usuario);
            material.setFechaSubida(LocalDateTime.now());

            // Si hay archivo, guardarlo
            if (archivo != null && !archivo.isEmpty()) {
                try {
                    String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
                    Path path = Paths.get(UPLOAD_DIR + nombreArchivo);
                    Files.write(path, archivo.getBytes());
                    material.setNombreArchivo(nombreArchivo);
                    material.setTipoArchivo(archivo.getContentType());
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "❌ Error al subir el archivo.");
                    return "redirect:/materiales";
                }
            }

            // Si hay enlace, guardarlo
            if (enlace != null && !enlace.trim().isEmpty()) {
                material.setEnlace(enlace);
                material.setTipoArchivo("ENLACE");
            }

            // Debe tener al menos archivo o enlace
            if (material.getNombreArchivo() == null && material.getEnlace() == null) {
                redirectAttributes.addFlashAttribute("error", "❌ Debes subir un archivo o proporcionar un enlace.");
                return "redirect:/materiales";
            }

            materialRepository.save(material);
            redirectAttributes.addFlashAttribute("success", "✅ Material subido exitosamente.");
        }

        return "redirect:/materiales";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Material material = materialRepository.findById(id).orElse(null);
        if (material != null) {
            if (material.getNombreArchivo() != null) {
                try {
                    Path path = Paths.get(UPLOAD_DIR + material.getNombreArchivo());
                    Files.deleteIfExists(path);
                } catch (IOException e) {}
            }
            materialRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "✅ Material eliminado.");
        }
        return "redirect:/materiales";
    }
} 