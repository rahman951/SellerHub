package com.sellerhub.auth.service;

import com.sellerhub.auth.model.User;

public interface EmailService {
    void sendVerificationEmail(User user, String token);
    void sendPasswordResetEmail(User user, String token);
}
