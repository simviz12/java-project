package com.productividadplus.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.*;

import java.time.LocalDate;

@Document(collection = "reportes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reporte {

    @Id
    private String id;

    private LocalDate fecha;

    private String descripcion;

    private int avancePorcentaje;

    private TipoReporte tipo;

    @DBRef
    private Tarea tarea;

    @DBRef
    private Usuario autor;
}
