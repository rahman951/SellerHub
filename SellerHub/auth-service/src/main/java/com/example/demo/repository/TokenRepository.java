package com.example.demo.repository;

import com.example.demo.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String refreshToken);

    List<Token> findAllByUser_IdAndLoggedOutFalse(Long userId);
}
