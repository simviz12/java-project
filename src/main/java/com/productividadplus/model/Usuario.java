package com.productividadplus.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    private String id;

    private String nombre;

    @Indexed(unique = true)
    private String email;

    private String contrasena;

    private boolean activo = true;

    private Rol rol;

    @Builder.Default
    private int intentosFallidos = 0;

    @Builder.Default
    private boolean cuentaBloqueada = false;

    private String tokenRecuperacion;

    @org.springframework.data.annotation.Transient
    @Builder.Default
    private List<Tarea> tareas = new ArrayList<>();
}
