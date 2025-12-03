package com.example.demo.service;

import com.example.demo.dto.AuthResponse;

public interface AuthService {
   AuthResponse login(String email, String password);
   AuthResponse refresh(String refreshToken);
}
