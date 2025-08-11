package com.sivalabs.urlshortener.controllers;

import com.sivalabs.urlshortener.dto.PagedResult;
import com.sivalabs.urlshortener.dto.ShortUrlDto;
import com.sivalabs.urlshortener.services.ShortUrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/short-urls")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @GetMapping
    public ResponseEntity<PagedResult<ShortUrlDto>> getPublicShortUrls(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        
        PagedResult<ShortUrlDto> result = shortUrlService.getPublicShortUrls(page);
        return ResponseEntity.ok(result);
    }
}