package com.productividadplus.controller;

import com.productividadplus.service.EstadisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/estadisticas")
@RequiredArgsConstructor
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    @GetMapping
    public String panel(Model model) {
        model.addAttribute("globales", estadisticaService.obtenerEstadisticasGlobales());
        model.addAttribute("porEmpleado", estadisticaService.obtenerEstadisticasPorEmpleado());
        return "estadisticas/panel";
    }
}
