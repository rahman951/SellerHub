package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    User user = new User();
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;


    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void add(User user) {
        User newUser = new User();

        if (!existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email уже занят");
        }
        newUser.setEmail(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(newUser);
    }

    @Override
    public void updateUser(User user) {
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getUsername());
        userRepository.save(newUser);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User showUser(long id) {
        return findById(id).orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

}