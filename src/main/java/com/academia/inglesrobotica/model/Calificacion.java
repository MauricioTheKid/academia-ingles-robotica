package com.academia.inglesrobotica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "calificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;

    // Evaluaciones parciales (4 períodos)
    @Column(name = "nota_parcial_1", precision = 4, scale = 1)
    private BigDecimal notaParcial1;

    @Column(name = "nota_parcial_2", precision = 4, scale = 1)
    private BigDecimal notaParcial2;

    @Column(name = "nota_parcial_3", precision = 4, scale = 1)
    private BigDecimal notaParcial3;

    @Column(name = "nota_parcial_4", precision = 4, scale = 1)
    private BigDecimal notaParcial4;

    // Proyecto
    @Column(name = "nota_proyecto", precision = 4, scale = 1)
    private BigDecimal notaProyecto;

    // Examen final
    @Column(name = "nota_examen_final", precision = 4, scale = 1)
    private BigDecimal notaExamenFinal;

    // Observaciones del profesor
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    // Método para calcular el promedio
    @Transient
    public BigDecimal getPromedio() {
        int count = 0;
        BigDecimal sum = BigDecimal.ZERO;

        if (notaParcial1 != null) { sum = sum.add(notaParcial1); count++; }
        if (notaParcial2 != null) { sum = sum.add(notaParcial2); count++; }
        if (notaParcial3 != null) { sum = sum.add(notaParcial3); count++; }
        if (notaParcial4 != null) { sum = sum.add(notaParcial4); count++; }
        if (notaProyecto != null) { sum = sum.add(notaProyecto); count++; }
        if (notaExamenFinal != null) { sum = sum.add(notaExamenFinal); count++; }

        if (count == 0) return BigDecimal.ZERO;
        return sum.divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP);
    }

    // Método para obtener estado
    @Transient
    public String getEstado() {
        BigDecimal promedio = getPromedio();
        if (promedio.compareTo(BigDecimal.ZERO) == 0) return "Sin notas";
        if (promedio.compareTo(new BigDecimal("7.0")) >= 0) return "Aprobado";
        if (promedio.compareTo(new BigDecimal("5.0")) >= 0) return "Regular";
        return "Reprobado";
    }

    // Método para color de estado
    @Transient
    public String getColorEstado() {
        switch (getEstado()) {
            case "Aprobado": return "success";
            case "Regular": return "warning";
            case "Reprobado": return "danger";
            default: return "secondary";
        }
    }
} 