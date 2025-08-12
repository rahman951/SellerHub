package com.example.demo.controller;

import com.example.demo.model.EmailVerificationToken;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        boolean success = userService.confirmEmail(token);

        if (success) {
            return ResponseEntity.ok("Email успешно подтверждён");
        } else {
            return ResponseEntity.badRequest().body("Токен не найден или истёк");
        }
    }
}
