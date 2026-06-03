package com.productividadplus.repository;

import com.productividadplus.model.EstadoProyecto;
import com.productividadplus.model.Proyecto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProyectoRepository extends MongoRepository<Proyecto, String> {

    List<Proyecto> findByEstado(EstadoProyecto estado);
}
