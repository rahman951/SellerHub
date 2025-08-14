package com.example.demo.service;

import com.example.demo.dto.PasswordResetRequest;
import com.example.demo.dto.RegistrationRequest;

public interface UserService {
    RegistrationRequest registerUser(String email,  String password);
    boolean confirmEmail(String token);
    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword);
}
