package com.academia.inglesrobotica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionRequest {

    @NotNull(message = "El ID de reserva es obligatorio")
    private Long reservaId;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El grado escolar es obligatorio")
    @Size(max = 100)
    private String gradoEscolar;

    @NotBlank(message = "El contacto de emergencia es obligatorio")
    @Size(max = 150)
    private String contactoEmergencia;

    @NotBlank(message = "El teléfono de emergencia es obligatorio")
    @Size(max = 20)
    private String telefonoEmergencia;

    private String alergias;

    private String condicionesMedicas;

    private String autorizacionFoto;

    private String tallaUniforme;
} 