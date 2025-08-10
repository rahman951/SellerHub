package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_tokens")
@Data
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    private String token;
    @ManyToOne
    private User user;
    private LocalDateTime expiryDate;

}
