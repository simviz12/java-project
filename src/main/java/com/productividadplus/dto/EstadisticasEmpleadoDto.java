package com.productividadplus.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasEmpleadoDto {

    private String empleadoId;
    private String empleadoNombre;
    private long tareasPendientes;
    private long tareasEnProgreso;
    private long tareasFinalizadas;
    private long totalReportes;
}
