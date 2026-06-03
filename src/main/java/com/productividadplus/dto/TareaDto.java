package com.productividadplus.dto;

import com.productividadplus.model.EstadoTarea;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TareaDto {

    private String id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "La fecha límite es obligatoria")
    @FutureOrPresent(message = "La fecha límite no puede estar en el pasado")
    private LocalDate fechaLimite;

    private EstadoTarea estado;

    @NotNull(message = "El responsable es obligatorio")
    private String responsableId;

    private String responsableNombre;

    @NotNull(message = "El proyecto es obligatorio")
    private String proyectoId;

    private String proyectoNombre;

    private boolean proximaAVencer;
}
