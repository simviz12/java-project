package com.productividadplus.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.productividadplus.dto.ReporteDto;
import com.productividadplus.exception.BusinessException;
import com.productividadplus.model.EstadoTarea;
import com.productividadplus.model.Reporte;
import com.productividadplus.model.Tarea;
import com.productividadplus.model.Usuario;
import com.productividadplus.repository.ReporteRepository;
import com.productividadplus.repository.TareaRepository;
import com.productividadplus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MongoTemplate mongoTemplate;

    @Transactional(readOnly = true)
    public List<ReporteDto> listarConFiltros(LocalDate fechaDesde, LocalDate fechaHasta, String autorId) {
        Query query = new Query();
        if (fechaDesde != null || fechaHasta != null) {
            Criteria criteria = Criteria.where("fecha");
            if (fechaDesde != null) criteria = criteria.gte(fechaDesde);
            if (fechaHasta != null) criteria = criteria.lte(fechaHasta);
            query.addCriteria(criteria);
        }
        if (autorId != null && !autorId.isEmpty()) {
            query.addCriteria(Criteria.where("autor.$id").is(new org.bson.types.ObjectId(autorId)));
        }
        return mongoTemplate.find(query, Reporte.class)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReporteDto> listarPorEmpleado(String empleadoId) {
        return reporteRepository.findByAutorId(empleadoId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReporteDto crear(ReporteDto dto, String autorId) {
        Tarea tarea = tareaRepository.findById(dto.getTareaId())
                .orElseThrow(() -> new BusinessException("Tarea no encontrada"));

        if (!tarea.getResponsable().getId().equals(autorId)) {
            throw new BusinessException("Solo puede reportar avance en sus propias tareas");
        }

        if (tarea.getEstado() == EstadoTarea.PENDIENTE) {
            throw new BusinessException(
                    "Solo puede crear reportes para tareas en estado EN_PROGRESO o FINALIZADA");
        }

        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        Reporte reporte = Reporte.builder()
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now())
                .descripcion(dto.getDescripcion())
                .avancePorcentaje(dto.getAvancePorcentaje())
                .tipo(dto.getTipo())
                .tarea(tarea)
                .autor(autor)
                .build();

        Reporte guardado = reporteRepository.save(reporte);
        log.info("Reporte creado id={} para tarea id={}", guardado.getId(), tarea.getId());
        return toDto(guardado);
    }

    @Transactional(readOnly = true)
    public byte[] exportarPdf(LocalDate fechaDesde, LocalDate fechaHasta, String autorId) {
        List<ReporteDto> reportes = listarConFiltros(fechaDesde, fechaHasta, autorId);
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
            document.add(new Paragraph("Reportes de Productividad - ProductividadPlus", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Generado: " + LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell(headerCell("Fecha"));
            table.addCell(headerCell("Empleado"));
            table.addCell(headerCell("Tarea"));
            table.addCell(headerCell("Avance %"));
            table.addCell(headerCell("Tipo"));

            for (ReporteDto r : reportes) {
                table.addCell(cell(r.getFecha() != null ? r.getFecha().toString() : ""));
                table.addCell(cell(r.getAutorNombre() != null ? r.getAutorNombre() : ""));
                table.addCell(cell(r.getTareaTitulo() != null ? r.getTareaTitulo() : ""));
                table.addCell(cell(String.valueOf(r.getAvancePorcentaje())));
                table.addCell(cell(r.getTipo() != null ? r.getTipo().name() : ""));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            log.error("Error al generar PDF", e);
            throw new BusinessException("Error al exportar reportes en PDF");
        }
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell cell(String text) {
        return new PdfPCell(new Phrase(text));
    }

    private ReporteDto toDto(Reporte reporte) {
        return ReporteDto.builder()
                .id(reporte.getId())
                .fecha(reporte.getFecha())
                .descripcion(reporte.getDescripcion())
                .avancePorcentaje(reporte.getAvancePorcentaje())
                .tipo(reporte.getTipo())
                .tareaId(reporte.getTarea().getId())
                .tareaTitulo(reporte.getTarea().getTitulo())
                .autorNombre(reporte.getAutor().getNombre())
                .build();
    }
}
