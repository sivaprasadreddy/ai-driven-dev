package com.sivalabs.urlshortener.services;

import com.sivalabs.urlshortener.dto.*;
import com.sivalabs.urlshortener.entities.User;
import com.sivalabs.urlshortener.exception.BadRequestException;
import com.sivalabs.urlshortener.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                      JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setName(request.name());
        user.setRole("ROLE_USER");

        User savedUser = userRepository.save(user);
        
        return new UserResponse(savedUser.getEmail(), savedUser.getName(), savedUser.getRole());
    }

    @Transactional(readOnly = true)
    public LoginResponse authenticateUser(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(user);
        Instant expiresAt = Instant.now().plus(jwtService.getExpirationTime(), ChronoUnit.SECONDS);

        return new LoginResponse(token, expiresAt, user.getEmail(), user.getName(), user.getRole());
    }
}