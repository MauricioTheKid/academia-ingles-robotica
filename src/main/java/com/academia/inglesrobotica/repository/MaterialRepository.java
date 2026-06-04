package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByCursoId(Long cursoId);
    List<Material> findByUsuarioId(Long usuarioId);
} 