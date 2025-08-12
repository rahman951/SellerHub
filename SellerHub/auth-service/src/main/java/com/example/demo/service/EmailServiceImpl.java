package com.example.demo.service;

import com.example.demo.model.User;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(User user, String token) {
        String confirmLink = baseUrl + "/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Подтверждение электронной почты");
        message.setText(
                "Здравствуйте!\n\n" +
                        "Чтобы подтвердить вашу почту, перейдите по ссылке:\n" +
                        confirmLink + "\n\n" +
                        "Если кнопка или ссылка не работают, скопируйте её в адресную строку браузера.\n\n" +
                        "Ссылка действительна 24 часа. Если срок истёк, запросите новую в личном кабинете.\n\n" +
                        "Если вы не регистрировались на нашем сервисе, просто проигнорируйте это письмо.\n\n" +
                        "С уважением,\n" +
                        "Команда поддержки"
        );

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(User user, String token) {
        String resetLink = baseUrl + "/password-reset?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Сброс пароля");
        message.setText(
                "Здравствуйте!\n\n" +
                        "Вы запросили сброс пароля для вашего аккаунта.\n" +
                        "Чтобы задать новый пароль, перейдите по ссылке:\n" +
                        resetLink + "\n\n" +
                        "Если вы не запрашивали сброс пароля, просто проигнорируйте это письмо.\n" +
                        "Ссылка будет действительна 24 часа.\n\n" +
                        "С уважением,\n" +
                        "Команда поддержки"
        );

        mailSender.send(message);
    }
}
