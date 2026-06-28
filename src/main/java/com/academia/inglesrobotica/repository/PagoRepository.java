package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    Optional<Pago> findByInscripcionId(Long inscripcionId);
    
    List<Pago> findByEstado(String estado);
    
    List<Pago> findByMetodoPago(String metodoPago);
} 