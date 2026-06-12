package com.academia.inglesrobotica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    // Datos del alumno
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "grado_escolar", length = 100)
    private String gradoEscolar;

    @Column(name = "contacto_emergencia", length = 150)
    private String contactoEmergencia;

    @Column(name = "telefono_emergencia", length = 20)
    private String telefonoEmergencia;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "condiciones_medicas", columnDefinition = "TEXT")
    private String condicionesMedicas;

    @Column(name = "autorizacion_foto", length = 10)
    private String autorizacionFoto = "NO";

    @Column(name = "talla_uniforme", length = 10)
    private String tallaUniforme;

    // Documentos
    @Column(name = "acta_nacimiento_url", length = 500)
    private String actaNacimientoUrl;

    @Column(name = "comprobante_pago_url", length = 500)
    private String comprobantePagoUrl;

    // Estado de la inscripción
    @Column(length = 30)
    private String estado = "PENDIENTE_PAGO"; // PENDIENTE_PAGO, PAGO_VERIFICACION, ACTIVA, CANCELADA, RECHAZADA

    @Column(name = "fecha_inscripcion")
    private LocalDateTime fechaInscripcion = LocalDateTime.now();

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(length = 500)
    private String observaciones;

    @OneToOne(mappedBy = "inscripcion", cascade = CascadeType.ALL)
    private Pago pago;
} 