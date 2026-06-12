package com.academia.inglesrobotica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal monto;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, COMPLETADO, RECHAZADO

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago; // TRANSFERENCIA, TARJETA, EFECTIVO

    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;

    @Column(name = "comprobante_url", length = 500)
    private String comprobanteUrl;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;
} 