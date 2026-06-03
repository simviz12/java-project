package com.productividadplus.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "tareas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarea {

    @Id
    private String id;

    private String titulo;

    private String descripcion;

    private LocalDate fechaLimite;

    private EstadoTarea estado;

    @DBRef
    private Usuario responsable;

    @DBRef
    private Proyecto proyecto;

    @org.springframework.data.annotation.Transient
    @Builder.Default
    private List<Reporte> reportes = new ArrayList<>();

    @Builder.Default
    private boolean notificacionVencimientoEnviada = false;
}
