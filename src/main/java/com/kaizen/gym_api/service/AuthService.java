package com.kaizen.gym_api.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kaizen.gym_api.dto.request.LoginRequest;
import com.kaizen.gym_api.dto.request.RegisterRequest;
import com.kaizen.gym_api.dto.response.AuthResponse;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.security.JwtService;
import com.kaizen.gym_api.repository.UserRepository;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.kaizen.gym_api.dto.request.GoogleLoginRequest;
import com.kaizen.gym_api.model.enums.AuthProvider;
import org.springframework.beans.factory.annotation.Value;
import java.util.Collections;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${google.client.id}")
    private String googleClientId;

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
        // Fetch user first to apply auth-provider specific login behavior.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> userRepository.findByUsername(request.getEmail())
                        .orElseThrow(() -> new BadCredentialsException("INVALID_CREDENTIALS")));

        // Accounts without local credentials must use OAuth login.
        if (user.getPasswordHash() == null) {
            throw new BadCredentialsException("OAUTH_ONLY_ACCOUNT");
        }

        // For LOCAL/BOTH accounts, invalid credentials must map to standard bad-credentials flow.
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    request.getPassword()
                )
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS");
        } catch (Exception ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS");
        }

        // Generate token
        String jwtToken = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPasswordHash() != null ? user.getPasswordHash() : "",
                    new java.util.ArrayList<>()
                ),
                user.getId()
        );

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    // ---------------
    // ---------------
    // GOOGLE LOGIN
    // ---------------
    // ---------------

    public AuthResponse googleLogin(GoogleLoginRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();

                User user = userRepository.findByEmail(email).orElse(null);

                if (user == null) {
                    // Generate unique username
                    String baseUsername = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
                    String newUsername = baseUsername;
                    int counter = 1;
                    while (userRepository.findByUsername(newUsername).isPresent()) {
                        newUsername = baseUsername + counter;
                        counter++;
                    }

                    // Create new user via Google
                    user = User.builder()
                            .username(newUsername)
                            .email(email)
                            .passwordHash(null)
                            .authProvider(AuthProvider.GOOGLE)
                            .restTimerDefault(90)
                            .build();
                    userRepository.save(user);
                } else if (user.getAuthProvider() == AuthProvider.LOCAL) {
                    // Account Linking
                    user.setAuthProvider(AuthProvider.BOTH);
                    userRepository.save(user);
                }

                // Generate token
                String jwtToken = jwtService.generateToken(
                        new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                user.getPasswordHash() != null ? user.getPasswordHash() : "",
                                new java.util.ArrayList<>()
                        ),
                        user.getId()
                );

                return AuthResponse.builder()
                        .token(jwtToken)
                        .build();
            } else {
                throw new IllegalArgumentException("Invalid Google ID token.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error verifying Google ID token: " + e.getMessage());
        }
    }
}


    // ---------------
    // ---------------
    // UPDATE
    // ---------------
    // ---------------