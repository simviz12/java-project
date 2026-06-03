package com.productividadplus.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "proyectos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proyecto {

    @Id
    private String id;

    private String nombre;

    private String descripcion;

    private LocalDate fechaInicio;

    private EstadoProyecto estado;

    @org.springframework.data.annotation.Transient
    @Builder.Default
    private List<Tarea> tareas = new ArrayList<>();
}
