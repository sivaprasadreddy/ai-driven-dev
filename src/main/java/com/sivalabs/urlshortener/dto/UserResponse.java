package com.sivalabs.urlshortener.dto;

public record UserResponse(
    String email,
    String name,
    String role
) {}