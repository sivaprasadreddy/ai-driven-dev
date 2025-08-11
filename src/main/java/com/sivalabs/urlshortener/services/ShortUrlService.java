package com.sivalabs.urlshortener.services;

import com.sivalabs.urlshortener.dto.PagedResult;
import com.sivalabs.urlshortener.dto.ShortUrlDto;
import com.sivalabs.urlshortener.dto.UserDto;
import com.sivalabs.urlshortener.entities.ShortUrl;
import com.sivalabs.urlshortener.exception.BadRequestException;
import com.sivalabs.urlshortener.repositories.ShortUrlRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public ShortUrlService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
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