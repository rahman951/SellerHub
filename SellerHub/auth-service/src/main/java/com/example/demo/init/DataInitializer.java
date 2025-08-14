package com.example.demo.init;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Создаём роли, если нет
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });

        Role sellerRole = roleRepository.findByName("SELLER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("SELLER");
                    return roleRepository.save(role);
                });

        // 2. Создаём пользователя-админа, если нет
        userRepository.findByEmail("admin@example.com")
                .orElseGet(() -> {
                    User admin = new User();
                    admin.setEmail("admin@example.com");
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.setEnabled(true);
                    admin.setRoles(Set.of(adminRole));
                    return userRepository.save(admin);
                });

        System.out.println("✅ Инициализация данных завершена");
    }
}
