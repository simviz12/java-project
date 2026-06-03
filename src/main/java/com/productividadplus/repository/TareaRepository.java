package com.productividadplus.repository;

import com.productividadplus.model.EstadoTarea;
import com.productividadplus.model.Tarea;
import com.productividadplus.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TareaRepository extends MongoRepository<Tarea, String> {

    List<Tarea> findByResponsable(Usuario responsable);

    List<Tarea> findByResponsableId(String responsableId);

    List<Tarea> findByEstado(EstadoTarea estado);

    long countByEstado(EstadoTarea estado);

    long countByResponsableIdAndEstado(String responsableId, EstadoTarea estado);

    @Query("{ 'estado': ?0, 'responsable.$id': ?1 }")
    List<Tarea> findByEstadoAndResponsableId(EstadoTarea estado, String responsableId);

    @Query("{ 'fechaLimite': { $gte: ?0, $lte: ?1 }, 'estado': { $ne: 'FINALIZADA' }, 'notificacionVencimientoEnviada': false }")
    List<Tarea> findTareasProximasAVencer(LocalDate hoy, LocalDate limite);
}
