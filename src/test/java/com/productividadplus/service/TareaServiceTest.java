package com.productividadplus.service;

import com.productividadplus.dto.TareaDto;
import com.productividadplus.exception.BusinessException;
import com.productividadplus.model.*;
import com.productividadplus.repository.ProyectoRepository;
import com.productividadplus.repository.TareaRepository;
import com.productividadplus.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TareaServiceTest {

    @Mock private TareaRepository tareaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProyectoRepository proyectoRepository;
    @Mock private NotificacionService notificacionService;

    @InjectMocks
    private TareaService tareaService;

    private Usuario empleado;
    private Proyecto proyecto;
    private Tarea tarea;

    @BeforeEach
    void setUp() {
        empleado = Usuario.builder().id(1L).nombre("Test").email("test@test.com").rol(Rol.EMPLEADO).build();
        proyecto = Proyecto.builder().id(1L).nombre("Proyecto").estado(EstadoProyecto.ACTIVO).build();
        tarea = Tarea.builder()
                .id(1L).titulo("Tarea").fechaLimite(LocalDate.now().plusDays(5))
                .estado(EstadoTarea.PENDIENTE).responsable(empleado).proyecto(proyecto).build();
    }

    @Test
    void cambiarEstado_avanzaDePendienteAEnProgreso() {
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        when(tareaRepository.save(any())).thenReturn(tarea);

        TareaDto result = tareaService.cambiarEstado(1L, EstadoTarea.EN_PROGRESO, 1L, false);

        assertEquals(EstadoTarea.EN_PROGRESO, result.getEstado());
    }

    @Test
    void cambiarEstado_noPermiteRetroceder() {
        tarea.setEstado(EstadoTarea.EN_PROGRESO);
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        assertThrows(BusinessException.class,
                () -> tareaService.cambiarEstado(1L, EstadoTarea.PENDIENTE, 1L, false));
    }

    @Test
    void crear_rechazaFechaPasada() {
        TareaDto dto = TareaDto.builder()
                .titulo("Nueva").fechaLimite(LocalDate.now().minusDays(1))
                .responsableId(1L).proyectoId(1L).build();

        assertThrows(BusinessException.class, () -> tareaService.crear(dto));
    }
}
