package com.productividadplus.service;

import com.productividadplus.model.Tarea;
import com.productividadplus.model.Usuario;
import com.productividadplus.repository.TareaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final JavaMailSender mailSender;
    private final TareaRepository tareaRepository;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public void enviarAsignacionTarea(Tarea tarea) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(tarea.getResponsable().getEmail());
            message.setSubject("[ProductividadPlus] Nueva tarea asignada");
            message.setText(String.format(
                    "Hola %s,\n\nSe te ha asignado la tarea \"%s\" con fecha límite %s.\n\n" +
                    "Accede al sistema: %s/mis-tareas\n\nSaludos,\nProductividadPlus",
                    tarea.getResponsable().getNombre(),
                    tarea.getTitulo(),
                    tarea.getFechaLimite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    baseUrl));
            mailSender.send(message);
            log.info("Notificación de asignación enviada para tarea id={}", tarea.getId());
        } catch (Exception e) {
            log.warn("No se pudo enviar correo de asignación para tarea id={}: {}",
                    tarea.getId(), e.getMessage());
        }
    }

    public void enviarRecuperacionPassword(Usuario usuario, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(usuario.getEmail());
            message.setSubject("[ProductividadPlus] Recuperación de contraseña");
            message.setText(String.format(
                    "Hola %s,\n\nPara restablecer tu contraseña accede al siguiente enlace:\n%s/reset-password/%s\n\n" +
                    "Si no solicitaste este cambio, ignora este mensaje.\n\nSaludos,\nProductividadPlus",
                    usuario.getNombre(), baseUrl, token));
            mailSender.send(message);
            log.info("Correo de recuperación enviado a usuario id={}", usuario.getId());
        } catch (Exception e) {
            log.warn("No se pudo enviar correo de recuperación: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void notificarTareasProximasAVencer() {
        LocalDate hoy = LocalDate.now();
        LocalDate manana = hoy.plusDays(1);
        List<Tarea> tareas = tareaRepository.findTareasProximasAVencer(manana, manana);

        for (Tarea tarea : tareas) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(mailFrom);
                message.setTo(tarea.getResponsable().getEmail());
                message.setSubject("[ProductividadPlus] Tarea próxima a vencer");
                message.setText(String.format(
                        "Hola %s,\n\nLa tarea \"%s\" vence mañana (%s).\n\n" +
                        "Por favor actualiza el avance en el sistema.\n\nSaludos,\nProductividadPlus",
                        tarea.getResponsable().getNombre(),
                        tarea.getTitulo(),
                        tarea.getFechaLimite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                mailSender.send(message);
                tarea.setNotificacionVencimientoEnviada(true);
                tareaRepository.save(tarea);
                log.info("Notificación de vencimiento enviada para tarea id={}", tarea.getId());
            } catch (Exception e) {
                log.warn("Error al notificar vencimiento tarea id={}: {}", tarea.getId(), e.getMessage());
            }
        }
    }
}
