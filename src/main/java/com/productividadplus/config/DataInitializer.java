package com.productividadplus.config;

import com.productividadplus.model.*;
import com.productividadplus.repository.ProyectoRepository;
import com.productividadplus.repository.TareaRepository;
import com.productividadplus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info("Datos ya cargados, omitiendo inicialización");
            return;
        }
        log.info("Cargando datos de prueba...");

        crearUsuario("Administrador", "admin@empresa.com", "Admin123", Rol.ADMINISTRADOR);
        crearUsuario("Gerente Uno", "gerente1@empresa.com", "Gerente123", Rol.GERENTE);
        crearUsuario("Gerente Dos", "gerente2@empresa.com", "Gerente123", Rol.GERENTE);
        Usuario emp1 = crearUsuario("Empleado Uno", "empleado1@empresa.com", "Empleado123", Rol.EMPLEADO);
        Usuario emp2 = crearUsuario("Empleado Dos", "empleado2@empresa.com", "Empleado123", Rol.EMPLEADO);
        Usuario emp3 = crearUsuario("Empleado Tres", "empleado3@empresa.com", "Empleado123", Rol.EMPLEADO);
        Usuario emp4 = crearUsuario("Empleado Cuatro", "empleado4@empresa.com", "Empleado123", Rol.EMPLEADO);

        Proyecto p1 = proyectoRepository.save(Proyecto.builder()
                .nombre("Sistema CRM")
                .descripcion("Implementación del módulo CRM")
                .fechaInicio(LocalDate.now().minusMonths(1))
                .estado(EstadoProyecto.ACTIVO)
                .build());

        Proyecto p2 = proyectoRepository.save(Proyecto.builder()
                .nombre("Portal Web")
                .descripcion("Rediseño del portal corporativo")
                .fechaInicio(LocalDate.now().minusWeeks(2))
                .estado(EstadoProyecto.ACTIVO)
                .build());

        crearTarea("Configurar base de datos", "Crear esquema MySQL", LocalDate.now().plusDays(5),
                emp1, p1, EstadoTarea.EN_PROGRESO);
        crearTarea("Diseñar API REST", "Endpoints principales", LocalDate.now().plusDays(10),
                emp2, p1, EstadoTarea.PENDIENTE);
        crearTarea("Pruebas unitarias", "Cobertura mínima 80%", LocalDate.now().plusDays(15),
                emp3, p1, EstadoTarea.PENDIENTE);

        crearTarea("Maquetar homepage", "Bootstrap 5 responsive", LocalDate.now().plusDays(7),
                emp2, p2, EstadoTarea.EN_PROGRESO);
        crearTarea("Integrar autenticación", "OAuth2 y sesiones", LocalDate.now().plusDays(12),
                emp4, p2, EstadoTarea.PENDIENTE);
        crearTarea("Optimizar rendimiento", "Lighthouse score > 90", LocalDate.now().plusDays(20),
                emp1, p2, EstadoTarea.PENDIENTE);

        log.info("Datos de prueba cargados: 7 usuarios, 2 proyectos, 6 tareas");
    }

    private Usuario crearUsuario(String nombre, String email, String pass, Rol rol) {
        return usuarioRepository.save(Usuario.builder()
                .nombre(nombre)
                .email(email)
                .contrasena(passwordEncoder.encode(pass))
                .activo(true)
                .rol(rol)
                .build());
    }

    private void crearTarea(String titulo, String desc, LocalDate fecha,
                            Usuario responsable, Proyecto proyecto, EstadoTarea estado) {
        tareaRepository.save(Tarea.builder()
                .titulo(titulo)
                .descripcion(desc)
                .fechaLimite(fecha)
                .estado(estado)
                .responsable(responsable)
                .proyecto(proyecto)
                .build());
    }
}
