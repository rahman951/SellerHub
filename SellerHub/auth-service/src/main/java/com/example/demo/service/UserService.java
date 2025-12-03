package com.example.demo.service;

public interface UserService {
    void registerUser(String email,  String password);
    boolean confirmEmail(String token);
    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword);
}
