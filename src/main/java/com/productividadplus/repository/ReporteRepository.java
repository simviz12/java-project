package com.productividadplus.repository;

import com.productividadplus.model.Reporte;
import com.productividadplus.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReporteRepository extends MongoRepository<Reporte, String> {

    List<Reporte> findByAutor(Usuario autor);

    List<Reporte> findByAutorId(String autorId);

    List<Reporte> findByTareaId(String tareaId);
}
