package com.productividadplus.security;

import com.productividadplus.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final int MAX_INTENTOS = 5;

    private final UsuarioRepository usuarioRepository;

    {
        setDefaultFailureUrl("/login?error=true");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        if (email != null) {
            usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                int intentos = usuario.getIntentosFallidos() + 1;
                usuario.setIntentosFallidos(intentos);
                if (intentos >= MAX_INTENTOS) {
                    usuario.setCuentaBloqueada(true);
                }
                usuarioRepository.save(usuario);
            });
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
