package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;


    @Override
    public void sendVerificationEmail(User user, String token) { // доработать
       SimpleMailMessage message = new SimpleMailMessage();
       message.setTo(user.getEmail());
       message.setSubject("Verification Email");
       message.setText("Your verification code has been sent to " + user.getEmail());
       mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(User user, String token) { // доработать
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Email");
        message.setText("Your password has been sent to " + user.getEmail());
    }
}
