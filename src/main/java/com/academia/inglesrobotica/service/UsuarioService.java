package com.academia.inglesrobotica.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.academia.inglesrobotica.model.Usuario;
import com.academia.inglesrobotica.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public List<Usuario> findByRolId(Long rolId) {
        return usuarioRepository.findByRolId(rolId);
    }

    public List<Usuario> findActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public Optional<Usuario> findByTokenReset(String token) {
        return usuarioRepository.findByTokenReset(token);
    }

    // NUEVO: Buscar hijos por padre
    public List<Usuario> findHijosByParentId(Long parentId) {
        return usuarioRepository.findByParentId(parentId);
    }
}