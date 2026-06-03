package com.productividadplus.dto;

import com.productividadplus.model.TipoReporte;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteDto {

    private String id;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Min(value = 0, message = "El avance mínimo es 0%")
    @Max(value = 100, message = "El avance máximo es 100%")
    private int avancePorcentaje;

    @NotNull(message = "El tipo de reporte es obligatorio")
    private TipoReporte tipo;

    @NotNull(message = "Debe seleccionar una tarea")
    private String tareaId;

    private String tareaTitulo;

    private String autorNombre;
}
