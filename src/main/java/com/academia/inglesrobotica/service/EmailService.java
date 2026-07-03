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

    public void enviarInicioInscripcion(String to, Long inscripcionId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("📝 Completa tu inscripción - Academia San Luis");
        message.setText("¡Hola!\n\n" +
                "Has dado el primer paso para formar parte de nuestra academia.\n\n" +
                "Para completar tu inscripción, sigue estos pasos:\n\n" +
                "1️⃣ Ingresa a tu perfil: http://localhost:8080/perfil\n" +
                "2️⃣ Ve a 'Mis Inscripciones'\n" +
                "3️⃣ Haz clic en 'Completar inscripción' para la reserva #" + inscripcionId + "\n" +
                "4️⃣ Sube tu acta de nacimiento\n" +
                "5️⃣ Realiza el pago y sube tu comprobante\n\n" +
                "¡Te esperamos!\n\n" +
                "Saludos,\nAcademia San Luis");
        mailSender.send(message);
    }

    public void enviarInscripcionExitosa(String to, String cursoNombre, Long inscripcionId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("🎉 ¡Inscripción confirmada! - Academia San Luis");
        message.setText("¡Felicidades!\n\n" +
                "Tu inscripción al curso " + cursoNombre + " ha sido CONFIRMADA.\n\n" +
                "Número de inscripción: " + inscripcionId + "\n\n" +
                "Próximamente recibirás más información sobre el inicio de clases.\n\n" +
                "Saludos,\nAcademia San Luis");
        mailSender.send(message);
    }

    public void enviarPagoVerificado(String to, boolean aprobado, String cursoNombre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        if (aprobado) {
            message.setSubject("✅ Pago verificado - Academia San Luis");
            message.setText("¡Excelente noticia!\n\n" +
                    "Tu pago para el curso " + cursoNombre + " ha sido VERIFICADO.\n\n" +
                    "Tu inscripción está ahora ACTIVA. Bienvenido a la academia.\n\n" +
                    "Saludos,\nAcademia San Luis");
        } else {
            message.setSubject("⚠️ Problema con tu pago - Academia San Luis");
            message.setText("Hola,\n\n" +
                    "Tu comprobante de pago para el curso " + cursoNombre + " ha sido RECHAZADO.\n\n" +
                    "Por favor, comunícate con la administración para regularizar tu situación.\n\n" +
                    "Saludos,\nAcademia San Luis");
        }
        mailSender.send(message);
    }

    // ===== NUEVO MÉTODO =====
    public void enviarCredencialesPadre(String to, String passwordTemporal) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("👨‍👩‍👧‍👦 Credenciales - Academia San Luis");
        message.setText("¡Bienvenido a Academia San Luis!\n\n" +
                "Se ha creado tu cuenta de Padre de Familia porque tu hijo(a) te ha registrado.\n\n" +
                "📧 Email: " + to + "\n" +
                "🔑 Contraseña temporal: " + passwordTemporal + "\n\n" +
                "Inicia sesión en: http://localhost:8080/auth/login\n\n" +
                "⚠️ Te recomendamos cambiar tu contraseña al iniciar sesión.\n\n" +
                "Desde tu panel podrás monitorear el progreso académico de tus hijos.\n\n" +
                "Saludos,\nAcademia San Luis");
        mailSender.send(message);
    }
}