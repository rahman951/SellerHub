package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(JwtUtil jwtUtil, UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Map<String, String> authenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        if(authenticationToken.isAuthenticated()) {
            authenticationManager.authenticate(authenticationToken);
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Токен невалиден или истёк");
        }
        User user = userRepository.findByEmail(email).orElse(null);
        Map<String, String> map = new HashMap<>();
        map.put("access", jwtUtil.generateAccessToken(user));
        //map.put("Refresh" jwtUtil.generateRefreshToken(user));
        return map;
    }
}
