package com.sellerhub.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshToken(@NotBlank String refreshToken) {}
