package com.productividadplus.controller;

import com.productividadplus.dto.TareaDto;
import com.productividadplus.model.EstadoTarea;
import com.productividadplus.repository.ProyectoRepository;
import com.productividadplus.security.CustomUserDetails;
import com.productividadplus.service.TareaService;
import com.productividadplus.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/tareas")
@RequiredArgsConstructor
public class TareaController {

    private final TareaService tareaService;
    private final UsuarioService usuarioService;
    private final ProyectoRepository proyectoRepository;

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoTarea estado,
                         @RequestParam(required = false) String responsableId,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLimite,
                         Model model) {
        model.addAttribute("tareas", tareaService.listarConFiltros(estado, responsableId, fechaLimite));
        model.addAttribute("empleados", usuarioService.findEmpleados());
        model.addAttribute("estados", EstadoTarea.values());
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroResponsable", responsableId);
        model.addAttribute("filtroFecha", fechaLimite);
        return "tareas/lista";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("tarea", new TareaDto());
        model.addAttribute("empleados", usuarioService.findEmpleados());
        model.addAttribute("proyectos", proyectoRepository.findAll());
        return "tareas/form";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("tarea") TareaDto tarea,
                        BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("empleados", usuarioService.findEmpleados());
            model.addAttribute("proyectos", proyectoRepository.findAll());
            return "tareas/form";
        }
        tareaService.crear(tarea);
        redirect.addFlashAttribute("success", "Tarea creada correctamente");
        return "redirect:/tareas";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable String id, Model model) {
        model.addAttribute("tarea", tareaService.obtenerPorId(id));
        model.addAttribute("empleados", usuarioService.findEmpleados());
        model.addAttribute("proyectos", proyectoRepository.findAll());
        return "tareas/form";
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable String id,
                         @Valid @ModelAttribute("tarea") TareaDto tarea,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("empleados", usuarioService.findEmpleados());
            model.addAttribute("proyectos", proyectoRepository.findAll());
            return "tareas/form";
        }
        tareaService.actualizar(id, tarea);
        redirect.addFlashAttribute("success", "Tarea actualizada correctamente");
        return "redirect:/tareas";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        tareaService.eliminar(id);
        redirect.addFlashAttribute("success", "Tarea eliminada");
        return "redirect:/tareas";
    }

    @GetMapping("/dashboard-gerente")
    public String dashboardGerenteRedirect() {
        return "redirect:/dashboard-gerente";
    }
}
