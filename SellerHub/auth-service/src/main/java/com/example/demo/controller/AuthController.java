package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    private final UserService userService;

    public AuthController(AuthService auth, UserService userService) {
        this.auth = auth;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest registration) {
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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse tokens = auth.login(request.email(), request.password());
        return ResponseEntity.ok(tokens);
    }


    @PostMapping("/token/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshToken request) {
        AuthResponse tokens = auth.refresh(request.refreshToken());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetEmailRequest request) {
        userService.initiatePasswordReset(request.email());
        return ResponseEntity.ok("Если пользователь существует, на почту отправлены инструкции по сбросу пароля.");
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        userService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok("Пароль успешно изменён.");
    }

    @GetMapping("/secure/test")
    @PreAuthorize("hasRole('SELLER')")
    public String test() {
        return "OK";
    }
}
