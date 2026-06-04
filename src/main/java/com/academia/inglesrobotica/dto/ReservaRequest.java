package com.academia.inglesrobotica.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequest {

    @NotNull(message = "El ID del horario es obligatorio")
    private Long horarioId;
}