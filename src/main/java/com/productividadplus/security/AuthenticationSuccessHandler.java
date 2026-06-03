package com.productividadplus.security;

import com.productividadplus.model.Rol;
import com.productividadplus.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            usuario.setIntentosFallidos(0);
            usuario.setCuentaBloqueada(false);
            usuarioRepository.save(usuario);
        });

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Rol rol = userDetails.getUsuario().getRol();

        String redirectUrl = switch (rol) {
            case ADMINISTRADOR -> "/admin";
            case GERENTE -> "/dashboard-gerente";
            case EMPLEADO -> "/dashboard-empleado";
        };

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
