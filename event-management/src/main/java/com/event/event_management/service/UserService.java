package com.event.event_management.service;

import com.event.event_management.dto.RegisterRequest;
import com.event.event_management.entity.User;
import com.event.event_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.event.event_management.dto.LoginRequest;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {

        User user = new User();

        user.setName(request.getName());
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword())); // encrypt later
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        return user;
    }


}