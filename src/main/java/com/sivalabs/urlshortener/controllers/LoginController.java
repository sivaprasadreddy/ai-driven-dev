package com.sivalabs.urlshortener.controllers;

import com.sivalabs.urlshortener.dto.LoginRequest;
import com.sivalabs.urlshortener.dto.LoginResponse;
import com.sivalabs.urlshortener.dto.RegisterRequest;
import com.sivalabs.urlshortener.dto.UserResponse;
import com.sivalabs.urlshortener.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.authenticateUser(request);
        return ResponseEntity.ok(response);
    }

}