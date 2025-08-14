package com.example.demo.dto;

public record PasswordResetRequest(String token, String newPassword) {}
