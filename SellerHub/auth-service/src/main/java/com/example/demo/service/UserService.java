package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    List<User> getAll();

    void add(User user);

    void updateUser(User user);

    void deleteUser(long id);

    User showUser(long id);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String username);

    Optional<User> findById(long id);

}
