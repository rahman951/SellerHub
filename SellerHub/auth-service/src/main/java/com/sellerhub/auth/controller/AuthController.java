package com.sellerhub.auth.controller;


import com.sellerhub.auth.dto.*;
import com.sellerhub.auth.service.AuthService;
import com.sellerhub.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final AuthService auth;
    private final UserService userService;

    public AuthController(AuthService auth, UserService userService) {
        this.auth = auth;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest registration) {
        userService.registerUser(registration.email(), registration.password());
        return ResponseEntity.ok("Регистрация успешна. Проверьте email для подтверждения.");
    }
    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        boolean confirmed = userService.confirmEmail(token);
        if (confirmed) {
            return ResponseEntity.ok("Email успешно подтверждён!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ссылка недействительна или срок её действия истёк.");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse tokens = auth.login(request.email(), request.password());
        return ResponseEntity.ok(tokens);
    }


    @PostMapping("/token/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshToken request) {
        AuthResponse tokens = auth.refresh(request.refreshToken());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody PasswordResetEmailRequest request) {
        userService.initiatePasswordReset(request.email());
        return ResponseEntity.ok("Если пользователь существует, на почту отправлены инструкции по сбросу пароля.");
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        userService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok("Пароль успешно изменён.");
    }
}
