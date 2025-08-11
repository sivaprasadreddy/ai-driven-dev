package com.sivalabs.urlshortener.repositories;

import com.sivalabs.urlshortener.entities.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    
    Optional<ShortUrl> findByShortKey(String shortKey);
    
    @Query("SELECT s FROM ShortUrl s WHERE s.isPrivate = false")
    Page<ShortUrl> findAllPublicShortUrls(Pageable pageable);
    
    Page<ShortUrl> findByCreatedByIdAndIsPrivate(Long userId, Boolean isPrivate, Pageable pageable);
    
    Page<ShortUrl> findByCreatedById(Long userId, Pageable pageable);
    
    boolean existsByShortKey(String shortKey);
}