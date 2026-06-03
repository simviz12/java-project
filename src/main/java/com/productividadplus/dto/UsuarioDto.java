package com.productividadplus.dto;

import com.productividadplus.model.Rol;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {

    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
             message = "La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número")
    private String contrasena;

    private boolean activo;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
}
