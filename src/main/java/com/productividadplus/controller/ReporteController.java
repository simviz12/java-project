package com.productividadplus.controller;

import com.productividadplus.dto.ReporteDto;
import com.productividadplus.model.EstadoTarea;
import com.productividadplus.model.TipoReporte;
import com.productividadplus.security.CustomUserDetails;
import com.productividadplus.service.ReporteService;
import com.productividadplus.service.TareaService;
import com.productividadplus.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final TareaService tareaService;
    private final UsuarioService usuarioService;

    @GetMapping("/globales")
    public String listarGlobales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) String autorId,
            Model model) {
        model.addAttribute("reportes", reporteService.listarConFiltros(fechaDesde, fechaHasta, autorId));
        model.addAttribute("empleados", usuarioService.findEmpleados());
        model.addAttribute("filtroFechaDesde", fechaDesde);
        model.addAttribute("filtroFechaHasta", fechaHasta);
        model.addAttribute("filtroAutor", autorId);
        return "reportes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        List<com.productividadplus.dto.TareaDto> tareas = tareaService.listarPorEmpleado(user.getId())
                .stream()
                .filter(t -> t.getEstado() == EstadoTarea.EN_PROGRESO
                          || t.getEstado() == EstadoTarea.FINALIZADA)
                .toList();
        model.addAttribute("reporte", ReporteDto.builder().fecha(LocalDate.now()).build());
        model.addAttribute("tareas", tareas);
        model.addAttribute("tipos", TipoReporte.values());
        return "reportes/form";
    }

    @PostMapping("/nuevo")
    public String crear(@Valid @ModelAttribute("reporte") ReporteDto reporte,
                        BindingResult result,
                        @AuthenticationPrincipal CustomUserDetails user,
                        Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("tareas", tareaService.listarPorEmpleado(user.getId()));
            model.addAttribute("tipos", TipoReporte.values());
            return "reportes/form";
        }
        reporteService.crear(reporte, user.getId());
        redirect.addFlashAttribute("success", "Reporte registrado correctamente");
        return "redirect:/dashboard-empleado";
    }

    @GetMapping("/mis-reportes")
    public String misReportes(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("reportes", reporteService.listarPorEmpleado(user.getId()));
        return "reportes/mis-reportes";
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) String autorId) {
        byte[] pdf = reporteService.exportarPdf(fechaDesde, fechaHasta, autorId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reportes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
