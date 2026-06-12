package com.academia.inglesrobotica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequest {

    @NotNull(message = "El ID de inscripción es obligatorio")
    private Long inscripcionId;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago; // TRANSFERENCIA, TARJETA, EFECTIVO

    private String referenciaPago;

    private BigDecimal monto;
} 