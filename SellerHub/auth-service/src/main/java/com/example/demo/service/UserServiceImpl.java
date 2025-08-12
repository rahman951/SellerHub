package com.example.demo.service;

import com.example.demo.model.EmailVerificationToken;
import com.example.demo.model.Token;
import com.example.demo.model.User;
import com.example.demo.repository.EmailVerificationTokenRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final Token token;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository, EmailVerificationTokenRepository emailVerificationTokenRepository, EmailService emailService, RoleRepository roleRepository, Token token) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.token = token;
    }


    @Override
    @Transactional
    public void add(User user) {
        User newUser = new User();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email уже занят");
        }
        newUser.setEmail(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEnabled(false);
        newUser.setRoles(user.getRoles());
        userRepository.save(newUser);
        String tok = UUID.randomUUID().toString();
        emailVerificationToken.setToken(tok);
        emailVerificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        emailVerificationTokenRepository.save(emailVerificationToken);
        emailService.sendVerificationEmail(newUser, tok);
    }

    @Override
    public boolean confirmEmail(String token) {
        return emailVerificationTokenRepository.findByToken(token)
                .filter(t -> LocalDateTime.now().isBefore(t.getExpiryDate()))
                .map(validToken -> {
                    // Активируем пользователя
                    User user = validToken.getUser();
                    user.setEnabled(true);
                    userRepository.save(user);

                    // Удаляем токен
                    emailVerificationTokenRepository.delete(validToken);

                    return true; // успех
                })
                .orElse(false); // токен не найден или истёк
    }


}