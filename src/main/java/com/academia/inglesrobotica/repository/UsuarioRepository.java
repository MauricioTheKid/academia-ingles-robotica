package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Usuario> findByRolId(Long rolId);
    
    List<Usuario> findByActivoTrue();

    Optional<Usuario> findByTokenReset(String tokenReset);
} 