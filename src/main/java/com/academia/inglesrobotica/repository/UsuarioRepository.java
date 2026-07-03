package com.academia.inglesrobotica.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.academia.inglesrobotica.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByRolId(Long rolId);

    List<Usuario> findByActivoTrue();

    Optional<Usuario> findByTokenReset(String tokenReset);

    // NUEVO: Buscar hijos por padre
    List<Usuario> findByParentId(Long parentId);
}