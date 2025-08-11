package com.sivalabs.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

public record CreateShortUrlRequest(
    @NotBlank(message = "Original URL is required")
    String originalUrl,
    
    Boolean isPrivate,
    
    @Min(value = 1, message = "Expiration days must be at least 1")
    Integer expirationInDays
) {}