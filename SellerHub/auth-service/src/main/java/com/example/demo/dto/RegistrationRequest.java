package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6, max = 64) String password
) {}
