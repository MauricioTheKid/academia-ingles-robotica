package com.academia.inglesrobotica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarConfirmacionReserva(String to, String curso, String dia, String horario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("✅ Reserva Confirmada - Academia San Luis");
        message.setText("¡Hola!\n\n" +
                "Tu reserva ha sido creada exitosamente:\n\n" +
                "📚 Curso: " + curso + "\n" +
                "📅 Día: " + dia + "\n" +
                "🕐 Horario: " + horario + "\n" +
                "📌 Estado: PENDIENTE (será confirmada por un administrador)\n\n" +
                "Gracias por confiar en Academia San Luis.\n\n" +
                "Saludos,\nAcademia San Luis");
        mailSender.send(message);
    }

    public void enviarRecuperacionPassword(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("🔑 Recuperación de Contraseña - Academia San Luis");
        message.setText("¡Hola!\n\n" +
                "Has solicitado restablecer tu contraseña.\n\n" +
                "Haz clic en el siguiente enlace:\n\n" +
                "http://localhost:8080/auth/restablecer?token=" + token + "\n\n" +
                "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                "Saludos,\nAcademia San Luis");
        mailSender.send(message);
    }
} 