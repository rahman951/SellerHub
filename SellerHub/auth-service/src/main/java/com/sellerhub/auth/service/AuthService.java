package com.sellerhub.auth.service;

import com.sellerhub.auth.dto.AuthResponse;

public interface AuthService {
   AuthResponse login(String email, String password);
   AuthResponse refresh(String refreshToken);
}
