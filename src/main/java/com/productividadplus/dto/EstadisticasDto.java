package com.productividadplus.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasDto {

    private long tareasPendientes;
    private long tareasEnProgreso;
    private long tareasFinalizadas;
    private long totalTareas;
    private long totalReportes;
}
