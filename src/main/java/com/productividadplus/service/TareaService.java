package com.productividadplus.service;

import com.productividadplus.dto.TareaDto;
import com.productividadplus.exception.BusinessException;
import com.productividadplus.model.*;
import com.productividadplus.repository.ProyectoRepository;
import com.productividadplus.repository.TareaRepository;
import com.productividadplus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

@Slf4j
@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final NotificacionService notificacionService;

    @Transactional(readOnly = true)
    public List<TareaDto> listarConFiltros(EstadoTarea estado, String responsableId, LocalDate fechaLimite) {
        Tarea exampleTarea = new Tarea();
        exampleTarea.setEstado(estado);
        if (responsableId != null && !responsableId.isEmpty()) {
            Usuario responsable = new Usuario();
            responsable.setId(responsableId);
            exampleTarea.setResponsable(responsable);
        }
        exampleTarea.setFechaLimite(fechaLimite);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        return tareaRepository.findAll(Example.of(exampleTarea, matcher))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TareaDto> listarPorEmpleado(String empleadoId) {
        return tareaRepository.findByResponsableId(empleadoId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TareaDto obtenerPorId(String id) {
        return toDto(findEntity(id));
    }

    @Transactional
    public TareaDto crear(TareaDto dto) {
        validarFechaLimite(dto.getFechaLimite());
        Usuario responsable = usuarioRepository.findById(dto.getResponsableId())
                .orElseThrow(() -> new BusinessException("Responsable no encontrado"));
        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new BusinessException("Proyecto no encontrado"));

        Tarea tarea = Tarea.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .fechaLimite(dto.getFechaLimite())
                .estado(EstadoTarea.PENDIENTE)
                .responsable(responsable)
                .proyecto(proyecto)
                .build();

        Tarea guardada = tareaRepository.save(tarea);
        notificacionService.enviarAsignacionTarea(guardada);
        log.info("Tarea creada id={} asignada a {}", guardada.getId(), responsable.getEmail());
        return toDto(guardada);
    }

    @Transactional
    public TareaDto actualizar(String id, TareaDto dto) {
        Tarea tarea = findEntity(id);
        validarFechaLimite(dto.getFechaLimite());

        tarea.setTitulo(dto.getTitulo());
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setFechaLimite(dto.getFechaLimite());

        if (dto.getResponsableId() != null && !dto.getResponsableId().equals(tarea.getResponsable().getId())) {
            Usuario nuevoResponsable = usuarioRepository.findById(dto.getResponsableId())
                    .orElseThrow(() -> new BusinessException("Responsable no encontrado"));
            tarea.setResponsable(nuevoResponsable);
            notificacionService.enviarAsignacionTarea(tarea);
        }

        if (dto.getProyectoId() != null) {
            Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                    .orElseThrow(() -> new BusinessException("Proyecto no encontrado"));
            tarea.setProyecto(proyecto);
        }

        return toDto(tareaRepository.save(tarea));
    }

    @Transactional
    public void eliminar(String id) {
        if (!tareaRepository.existsById(id)) {
            throw new BusinessException("Tarea no encontrada");
        }
        tareaRepository.deleteById(id);
        log.info("Tarea eliminada id={}", id);
    }

    @Transactional
    public TareaDto cambiarEstado(String id, EstadoTarea nuevoEstado, String usuarioId, boolean esGerente) {
        Tarea tarea = findEntity(id);

        if (!esGerente && !tarea.getResponsable().getId().equals(usuarioId)) {
            throw new BusinessException("No tiene permiso para modificar esta tarea");
        }

        validarTransicionEstado(tarea.getEstado(), nuevoEstado);
        tarea.setEstado(nuevoEstado);
        return toDto(tareaRepository.save(tarea));
    }

    @Transactional(readOnly = true)
    public void verificarPropiedad(String tareaId, String usuarioId) {
        Tarea tarea = findEntity(tareaId);
        if (!tarea.getResponsable().getId().equals(usuarioId)) {
            throw new BusinessException("No tiene permiso para acceder a esta tarea");
        }
    }

    private void validarTransicionEstado(EstadoTarea actual, EstadoTarea nuevo) {
        if (actual == nuevo) {
            return;
        }
        boolean valido = switch (actual) {
            case PENDIENTE -> nuevo == EstadoTarea.EN_PROGRESO;
            case EN_PROGRESO -> nuevo == EstadoTarea.FINALIZADA;
            case FINALIZADA -> false;
        };
        if (!valido) {
            throw new BusinessException(
                    "El estado solo puede avanzar: PENDIENTE → EN_PROGRESO → FINALIZADA");
        }
    }

    private void validarFechaLimite(LocalDate fecha) {
        if (fecha != null && fecha.isBefore(LocalDate.now())) {
            throw new BusinessException("No se puede asignar una tarea con fecha límite en el pasado");
        }
    }

    private Tarea findEntity(String id) {
        return tareaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tarea no encontrada"));
    }

    private TareaDto toDto(Tarea tarea) {
        LocalDate hoy = LocalDate.now();
        boolean proxima = tarea.getFechaLimite() != null
                && !tarea.getFechaLimite().isBefore(hoy)
                && !tarea.getFechaLimite().isAfter(hoy.plusDays(3))
                && tarea.getEstado() != EstadoTarea.FINALIZADA;

        return TareaDto.builder()
                .id(tarea.getId())
                .titulo(tarea.getTitulo())
                .descripcion(tarea.getDescripcion())
                .fechaLimite(tarea.getFechaLimite())
                .estado(tarea.getEstado())
                .responsableId(tarea.getResponsable().getId())
                .responsableNombre(tarea.getResponsable().getNombre())
                .proyectoId(tarea.getProyecto().getId())
                .proyectoNombre(tarea.getProyecto().getNombre())
                .proximaAVencer(proxima)
                .build();
    }
}
