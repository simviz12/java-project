package com.productividadplus.service;

import com.productividadplus.dto.UsuarioDto;
import com.productividadplus.exception.BusinessException;
import com.productividadplus.model.Rol;
import com.productividadplus.model.Usuario;
import com.productividadplus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificacionService notificacionService;

    @Transactional(readOnly = true)
    public Usuario findById(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Usuario> findEmpleados() {
        return usuarioRepository.findByRol(Rol.EMPLEADO);
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public void solicitarRecuperacionPassword(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString();
            usuario.setTokenRecuperacion(token);
            usuarioRepository.save(usuario);
            notificacionService.enviarRecuperacionPassword(usuario, token);
            log.info("Solicitud de recuperación de contraseña para usuario id={}", usuario.getId());
        });
    }

    @Transactional
    public void resetearPassword(String token, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacion(token)
                .orElseThrow(() -> new BusinessException("Token inválido o expirado"));
        validarContrasena(nuevaContrasena);
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuario.setTokenRecuperacion(null);
        usuario.setIntentosFallidos(0);
        usuario.setCuentaBloqueada(false);
        usuarioRepository.save(usuario);
        log.info("Contraseña restablecida para usuario id={}", usuario.getId());
    }

    @Transactional
    public Usuario crear(UsuarioDto dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }
        validarContrasena(dto.getContrasena());
        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .contrasena(passwordEncoder.encode(dto.getContrasena()))
                .activo(true)
                .rol(dto.getRol())
                .build();
        return usuarioRepository.save(usuario);
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || !contrasena.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new BusinessException(
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número");
        }
    }
}
