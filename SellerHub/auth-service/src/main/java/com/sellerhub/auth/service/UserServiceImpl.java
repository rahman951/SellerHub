package com.sellerhub.auth.service;

import com.sellerhub.auth.model.EmailVerificationToken;
import com.sellerhub.auth.model.PasswordResetToken;
import com.sellerhub.auth.model.User;
import com.sellerhub.auth.repository.EmailVerificationTokenRepository;
import com.sellerhub.auth.repository.PasswordResetTokenRepository;
import com.sellerhub.auth.repository.RoleRepository;
import com.sellerhub.auth.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           EmailVerificationTokenRepository emailVerificationTokenRepository,
                           EmailService emailService, RoleRepository roleRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    @Transactional
    public void registerUser(String email,  String password) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email обязателен");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email уже занят");
        }

        var defaultRole = roleRepository.findByName("SELLER")
                .orElseThrow(() -> new IllegalStateException("Роль SELLER не найдена"));

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEnabled(false);
        newUser.setRoles(java.util.Set.of(defaultRole));
        userRepository.save(newUser);

        emailVerificationTokenRepository.deleteByUser(newUser);

        String token = java.util.UUID.randomUUID().toString();
        EmailVerificationToken evt = new EmailVerificationToken();
        evt.setToken(token);
        evt.setUser(newUser);
        evt.setExpiryDate(java.time.LocalDateTime.now().plusHours(1));
        emailVerificationTokenRepository.save(evt);

        emailService.sendVerificationEmail(newUser, token);
    }

    @Override
    @Transactional
    public boolean confirmEmail(String token) {
        return emailVerificationTokenRepository.findByToken(token)
                .filter(t -> LocalDateTime.now().isBefore(t.getExpiryDate()))
                .map(validToken -> {
                    User user = validToken.getUser();
                    user.setEnabled(true);
                    userRepository.save(user);
                    emailVerificationTokenRepository.delete(validToken);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();

            PasswordResetToken prt = new PasswordResetToken();
            prt.setToken(token);
            prt.setUser(user);
            prt.setExpiryDate(LocalDateTime.now().plusHours(1));
            passwordResetTokenRepository.save(prt);

            emailService.sendPasswordResetEmail(user, token);
        });
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = passwordResetTokenRepository.findByToken(token)
                .filter(t -> LocalDateTime.now().isBefore(t.getExpiryDate()))
                .orElseThrow(() -> new IllegalArgumentException("Неверный или просроченный токен"));

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(prt);
    }
}
