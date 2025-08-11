package com.sivalabs.urlshortener.services;

import com.sivalabs.urlshortener.dto.CreateShortUrlRequest;
import com.sivalabs.urlshortener.dto.PagedResult;
import com.sivalabs.urlshortener.dto.ShortUrlDto;
import com.sivalabs.urlshortener.dto.UserDto;
import com.sivalabs.urlshortener.entities.ShortUrl;
import com.sivalabs.urlshortener.entities.User;
import com.sivalabs.urlshortener.exception.BadRequestException;
import com.sivalabs.urlshortener.repositories.ShortUrlRepository;
import com.sivalabs.urlshortener.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom;
    private static final String SHORT_KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public ShortUrlService(ShortUrlRepository shortUrlRepository, UserRepository userRepository) {
        this.shortUrlRepository = shortUrlRepository;
        this.userRepository = userRepository;
        this.secureRandom = new SecureRandom();
    }

    @Transactional(readOnly = true)
    public PagedResult<ShortUrlDto> getPublicShortUrls(int page) {
        // Page numbers in API start from 1, but Spring uses 0-based indexing
        int zeroBasedPage = Math.max(0, page - 1);
        
        // Default page size of 10, sorted by created_at descending (newest first)
        Pageable pageable = PageRequest.of(zeroBasedPage, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<ShortUrl> shortUrlPage = shortUrlRepository.findAllPublicShortUrls(pageable);
        int totalPages = shortUrlPage.getTotalPages();
        if(page > totalPages) {
            throw new BadRequestException("Invalid page number. Max valid page number is "+ totalPages);
        }
        Page<ShortUrlDto> dtoPage = shortUrlPage.map(this::convertToDto);
        
        return PagedResult.of(dtoPage);
    }

    @Transactional
    public ShortUrlDto createShortUrl(CreateShortUrlRequest request) {
        String normalizedUrl = normalizeUrl(request.originalUrl());
        validateUrl(normalizedUrl);
        
        User currentUser = getCurrentUser();
        
        String shortKey = generateUniqueShortKey();
        
        LocalDateTime expiresAt = null;
        if (request.expirationInDays() != null && request.expirationInDays() > 0) {
            expiresAt = LocalDateTime.now().plusDays(request.expirationInDays());
        }
        
        ShortUrl shortUrl = new ShortUrl(
            shortKey,
            normalizedUrl,
            currentUser,
            request.isPrivate() != null ? request.isPrivate() : false,
            expiresAt
        );
        
        ShortUrl savedShortUrl = shortUrlRepository.save(shortUrl);
        return convertToDto(savedShortUrl);
    }
    
    private String normalizeUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new BadRequestException("Original URL cannot be empty");
        }
        
        String trimmedUrl = url.trim();
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            return "http://" + trimmedUrl;
        }
        return trimmedUrl;
    }
    
    private void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            if (uri.getHost() == null || uri.getHost().trim().isEmpty()) {
                throw new BadRequestException("Invalid URL: missing host");
            }
            
            // Additional validation for host format
            String host = uri.getHost();
            if (!host.contains(".") || host.startsWith(".") || host.endsWith(".")) {
                throw new BadRequestException("Invalid URL: invalid host format");
            }
        } catch (URISyntaxException e) {
            throw new BadRequestException("Invalid URL format: " + e.getMessage());
        }
    }
    
    private User getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
            }
        } catch (Exception e) {
            // User not authenticated
        }
        return null;
    }
    
    private String generateUniqueShortKey() {
        String shortKey;
        int attempts = 0;
        int maxAttempts = 10;
        
        do {
            shortKey = generateRandomShortKey();
            attempts++;
            if (attempts >= maxAttempts) {
                throw new RuntimeException("Failed to generate unique short key after " + maxAttempts + " attempts");
            }
        } while (shortUrlRepository.existsByShortKey(shortKey));
        
        return shortKey;
    }
    
    private String generateRandomShortKey() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = secureRandom.nextInt(SHORT_KEY_CHARS.length());
            sb.append(SHORT_KEY_CHARS.charAt(index));
        }
        return sb.toString();
    }

    private ShortUrlDto convertToDto(ShortUrl shortUrl) {
        UserDto createdByDto = null;
        if (shortUrl.getCreatedBy() != null) {
            createdByDto = new UserDto(
                shortUrl.getCreatedBy().getId(),
                shortUrl.getCreatedBy().getName()
            );
        }

        return new ShortUrlDto(
            shortUrl.getId(),
            shortUrl.getShortKey(),
            shortUrl.getOriginalUrl(),
            shortUrl.getIsPrivate(),
            createdByDto,
            shortUrl.getClickCount(),
            shortUrl.getCreatedAt(),
            shortUrl.getExpiresAt()
        );
    }
}