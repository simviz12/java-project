package com.productividadplus.service;

import com.productividadplus.dto.UsuarioDto;
import com.productividadplus.exception.BusinessException;
import com.productividadplus.model.Rol;
import com.productividadplus.model.Usuario;
import com.productividadplus.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private NotificacionService notificacionService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void crear_rechazaEmailDuplicado() {
        when(usuarioRepository.existsByEmail("test@test.com")).thenReturn(true);
        UsuarioDto dto = UsuarioDto.builder()
                .nombre("Test").email("test@test.com")
                .contrasena("Password1").rol(Rol.EMPLEADO).build();

        assertThrows(BusinessException.class, () -> usuarioService.crear(dto));
    }

    @Test
    void resetearPassword_rechazaTokenInvalido() {
        when(usuarioRepository.findByTokenRecuperacion("invalid")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> usuarioService.resetearPassword("invalid", "Password1"));
    }

    @Test
    void crear_exito() {
        UsuarioDto dto = UsuarioDto.builder()
                .nombre("Nuevo").email("nuevo@test.com")
                .contrasena("Password1").rol(Rol.EMPLEADO).build();
        when(usuarioRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1")).thenReturn("hash");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario result = usuarioService.crear(dto);

        assertEquals("nuevo@test.com", result.getEmail());
        verify(usuarioRepository).save(any());
    }
}
