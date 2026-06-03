package com.productividadplus.repository;

import com.productividadplus.model.Rol;
import com.productividadplus.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByRol(Rol rol);

    Optional<Usuario> findByTokenRecuperacion(String token);
}
