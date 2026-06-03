package com.productividadplus.controller;

import com.productividadplus.model.EstadoTarea;
import com.productividadplus.security.CustomUserDetails;
import com.productividadplus.service.ReporteService;
import com.productividadplus.service.TareaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class EmpleadoController {

    private final TareaService tareaService;
    private final ReporteService reporteService;

    @GetMapping("/dashboard-empleado")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        String id = user.getId();
        model.addAttribute("tareas", tareaService.listarPorEmpleado(id));
        model.addAttribute("reportes", reporteService.listarPorEmpleado(id));
        model.addAttribute("nombre", user.getNombre());
        return "dashboard-empleado";
    }

    @GetMapping("/mis-tareas")
    public String misTareas(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("tareas", tareaService.listarPorEmpleado(user.getId()));
        model.addAttribute("estados", EstadoTarea.values());
        return "tareas/mis-tareas";
    }

    @PostMapping("/mis-tareas/{id}/estado")
    public String cambiarEstado(@PathVariable String id,
                                  @RequestParam EstadoTarea estado,
                                  @AuthenticationPrincipal CustomUserDetails user,
                                  RedirectAttributes redirect) {
        tareaService.cambiarEstado(id, estado, user.getId(), false);
        redirect.addFlashAttribute("success", "Estado actualizado");
        return "redirect:/mis-tareas";
    }
}
