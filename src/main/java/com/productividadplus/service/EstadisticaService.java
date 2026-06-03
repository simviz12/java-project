package com.productividadplus.service;

import com.productividadplus.dto.EstadisticasDto;
import com.productividadplus.dto.EstadisticasEmpleadoDto;
import com.productividadplus.model.EstadoTarea;
import com.productividadplus.model.Rol;
import com.productividadplus.model.Usuario;
import com.productividadplus.repository.ReporteRepository;
import com.productividadplus.repository.TareaRepository;
import com.productividadplus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstadisticaService {

    private final TareaRepository tareaRepository;
    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public EstadisticasDto obtenerEstadisticasGlobales() {
        long pendientes = tareaRepository.countByEstado(EstadoTarea.PENDIENTE);
        long enProgreso = tareaRepository.countByEstado(EstadoTarea.EN_PROGRESO);
        long finalizadas = tareaRepository.countByEstado(EstadoTarea.FINALIZADA);

        return EstadisticasDto.builder()
                .tareasPendientes(pendientes)
                .tareasEnProgreso(enProgreso)
                .tareasFinalizadas(finalizadas)
                .totalTareas(pendientes + enProgreso + finalizadas)
                .totalReportes(reporteRepository.count())
                .build();
    }

    @Transactional(readOnly = true)
    public List<EstadisticasEmpleadoDto> obtenerEstadisticasPorEmpleado() {
        List<Usuario> empleados = usuarioRepository.findByRol(Rol.EMPLEADO);
        return empleados.stream().map(emp -> EstadisticasEmpleadoDto.builder()
                .empleadoId(emp.getId())
                .empleadoNombre(emp.getNombre())
                .tareasPendientes(tareaRepository.countByResponsableIdAndEstado(emp.getId(), EstadoTarea.PENDIENTE))
                .tareasEnProgreso(tareaRepository.countByResponsableIdAndEstado(emp.getId(), EstadoTarea.EN_PROGRESO))
                .tareasFinalizadas(tareaRepository.countByResponsableIdAndEstado(emp.getId(), EstadoTarea.FINALIZADA))
                .totalReportes(reporteRepository.findByAutorId(emp.getId()).size())
                .build()).collect(Collectors.toList());
    }
}
