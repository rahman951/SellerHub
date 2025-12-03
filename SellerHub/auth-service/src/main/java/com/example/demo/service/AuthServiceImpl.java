package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.AuthResponse;
import com.example.demo.model.Token;
import com.example.demo.model.User;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           TokenRepository tokenRepository,
                           JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse login(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Неверный email или пароль");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));

        String access = jwtUtil.generateAccessToken(user);
        String refresh = jwtUtil.generateRefreshToken(user);

        Token rec = new Token();
        rec.setAccessToken(access);
        rec.setRefreshToken(refresh);
        rec.setLoggedOut(false);
        rec.setUser(user);
        tokenRepository.save(rec);

        return new AuthResponse(access, refresh);
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtil.isRefreshTokenValid(refreshToken)) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Невалидный или просроченный refresh-токен");
        }

        Token existing = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Refresh-токен не найден"));

        if (existing.isLoggedOut()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Refresh-токен отозван");
        }

        User user = existing.getUser();
        existing.setLoggedOut(true);
        tokenRepository.save(existing);

        String newAccess = jwtUtil.generateAccessToken(user);
        String newRefresh = jwtUtil.generateRefreshToken(user);

        Token rotated = new Token();
        rotated.setAccessToken(newAccess);
        rotated.setRefreshToken(newRefresh);
        rotated.setLoggedOut(false);
        rotated.setUser(user);
        tokenRepository.save(rotated);

        return new AuthResponse(newAccess, newRefresh);
    }

    // Кастомное исключение
    public static class AuthException extends RuntimeException {
        private final HttpStatus status;
        private final String message;

        public AuthException(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        public HttpStatus getStatus() {
            return status;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
