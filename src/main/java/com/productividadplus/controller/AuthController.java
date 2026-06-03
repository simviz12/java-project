package com.productividadplus.controller;

import com.productividadplus.service.UsuarioService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        @RequestParam(required = false) String expired,
                        Model model) {
        if (error != null) model.addAttribute("error", "Credenciales incorrectas o cuenta bloqueada");
        if (logout != null) model.addAttribute("success", "Sesión cerrada correctamente");
        if (expired != null) model.addAttribute("error", "Su sesión ha expirado");
        return "login";
    }

    @GetMapping("/recuperar-password")
    public String recuperarPasswordForm() {
        return "recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String recuperarPassword(@RequestParam @Email String email, RedirectAttributes redirect) {
        usuarioService.solicitarRecuperacionPassword(email);
        redirect.addFlashAttribute("success",
                "Si el email existe, recibirá instrucciones para restablecer su contraseña");
        return "redirect:/login";
    }

    @GetMapping("/reset-password/{token}")
    public String resetPasswordForm(@PathVariable String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password/{token}")
    public String resetPassword(@PathVariable String token,
                                @Validated @ModelAttribute ResetPasswordForm form,
                                RedirectAttributes redirect,
                                Model model) {
        if (!form.getContrasena().equals(form.getConfirmarContrasena())) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "reset-password";
        }
        usuarioService.resetearPassword(token, form.getContrasena());
        redirect.addFlashAttribute("success", "Contraseña actualizada. Puede iniciar sesión.");
        return "redirect:/login";
    }

    @Data
    public static class ResetPasswordForm {
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
                 message = "Mínimo 8 caracteres, una mayúscula y un número")
        private String contrasena;
        @NotBlank
        private String confirmarContrasena;
    }
}
