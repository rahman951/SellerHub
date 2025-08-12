package com.example.demo.service;

import java.util.Map;

public interface AuthService {
    Map<String, String> authenticate(String email, String password);
}
