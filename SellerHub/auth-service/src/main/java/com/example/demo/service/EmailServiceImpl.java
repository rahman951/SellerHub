package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String token) {
        String link = baseUrl + "/auth/confirm?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.getEmail());
        message.setSubject("Подтверждение email");
        message.setText("Здравствуйте.\n\nДля подтверждения вашей электронной почты перейдите по ссылке:\n" + link + "\n\nЕсли вы не регистрировались, проигнорируйте это письмо.");
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(User user, String token) {
        String link = baseUrl + "/auth/password-reset?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.getEmail());
        message.setSubject("Сброс пароля");
        message.setText("Здравствуйте.\n\nДля сброса пароля перейдите по ссылке:\n" + link + "\n\nЕсли вы не запрашивали сброс, проигнорируйте это письмо.");
        mailSender.send(message);
    }
}
