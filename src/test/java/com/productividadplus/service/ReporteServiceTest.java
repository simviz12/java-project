package com.productividadplus.service;

import com.productividadplus.dto.ReporteDto;
import com.productividadplus.exception.BusinessException;
import com.productividadplus.model.*;
import com.productividadplus.repository.ReporteRepository;
import com.productividadplus.repository.TareaRepository;
import com.productividadplus.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock private ReporteRepository reporteRepository;
    @Mock private TareaRepository tareaRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ReporteService reporteService;

    private Usuario empleado;
    private Tarea tarea;

    @BeforeEach
    void setUp() {
        empleado = Usuario.builder().id(1L).nombre("Emp").email("emp@test.com").rol(Rol.EMPLEADO).build();
        tarea = Tarea.builder()
                .id(1L).titulo("Tarea").estado(EstadoTarea.PENDIENTE)
                .responsable(empleado).build();
    }

    @Test
    void crear_rechazaTareaPendiente() {
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        ReporteDto dto = ReporteDto.builder()
                .tareaId(1L).descripcion("Avance").avancePorcentaje(50)
                .tipo(TipoReporte.DIARIO).build();

        assertThrows(BusinessException.class, () -> reporteService.crear(dto, 1L));
    }

    @Test
    void crear_rechazaTareaDeOtroEmpleado() {
        tarea.setEstado(EstadoTarea.EN_PROGRESO);
        tarea.getResponsable().setId(99L);
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        ReporteDto dto = ReporteDto.builder()
                .tareaId(1L).descripcion("Avance").avancePorcentaje(50)
                .tipo(TipoReporte.DIARIO).build();

        assertThrows(BusinessException.class, () -> reporteService.crear(dto, 1L));
    }
}
