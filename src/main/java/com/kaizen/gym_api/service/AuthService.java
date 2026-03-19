package com.kaizen.gym_api.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kaizen.gym_api.dto.request.LoginRequest;
import com.kaizen.gym_api.dto.request.RegisterRequest;
import com.kaizen.gym_api.dto.response.AuthResponse;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.UserRepository;
import com.kaizen.gym_api.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // ---------------
    // ---------------
    // REGISTER
    // ---------------
    // ---------------

    public String register(RegisterRequest request) {
        // Validate email and username exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Build new user (hashing password)
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .restTimerDefault(90)
                .build();

        // Save user in database
        userRepository.save(user);

        return "User registered successfully";
    }

    // ---------------
    // ---------------
    // LOGIN
    // ---------------
    // ---------------

    public AuthResponse login(LoginRequest request) {
        // Authenticate user credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Fetch user because we need it to generate token and get user ID
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> userRepository.findByUsername(request.getEmail())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid email or password")));

        // Generate token
        String jwtToken = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPasswordHash(),
                    new java.util.ArrayList<>()
                ),
                user.getId()
        );

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}


    // ---------------
    // ---------------
    // UPDATE
    // ---------------
    // ---------------