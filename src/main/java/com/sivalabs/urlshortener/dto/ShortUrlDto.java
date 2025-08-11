package com.sivalabs.urlshortener.dto;

import java.time.LocalDateTime;

public record ShortUrlDto(
    Long id,
    String shortKey,
    String originalUrl,
    Boolean isPrivate,
    UserDto createdBy,
    Long clickCount,
    LocalDateTime createdAt,
    LocalDateTime expiresAt
) {}