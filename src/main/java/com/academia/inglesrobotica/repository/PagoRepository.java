package com.academia.inglesrobotica.repository;

import com.academia.inglesrobotica.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    Optional<Pago> findByReservaId(Long reservaId);
    
    List<Pago> findByEstado(String estado);
    
    List<Pago> findByMetodoPago(String metodoPago);
}