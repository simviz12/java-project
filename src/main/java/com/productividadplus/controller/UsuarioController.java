package com.productividadplus.controller;

import com.productividadplus.dto.UsuarioDto;
import com.productividadplus.model.Rol;
import com.productividadplus.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public String adminHome(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "admin/index";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", UsuarioDto.builder().activo(true).build());
        model.addAttribute("roles", Rol.values());
        return "admin/usuario-form";
    }

    @PostMapping("/usuarios/nuevo")
    public String crearUsuario(@Valid @ModelAttribute("usuario") UsuarioDto usuario,
                               BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            return "admin/usuario-form";
        }
        usuarioService.crear(usuario);
        redirect.addFlashAttribute("success", "Usuario creado");
        return "redirect:/admin";
    }
}
