package com.productividadplus.controller;

import com.productividadplus.service.EstadisticaService;
import com.productividadplus.service.TareaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class GerenteController {

    private final EstadisticaService estadisticaService;
    private final TareaService tareaService;

    @GetMapping("/dashboard-gerente")
    public String dashboard(Model model) {
        model.addAttribute("estadisticas", estadisticaService.obtenerEstadisticasGlobales());
        model.addAttribute("tareas", tareaService.listarConFiltros(null, null, null));
        return "dashboard-gerente";
    }
}
