package com.sivalabs.urlshortener.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResult<T>(
        List<T> data,
        int pageNumber,
        int totalPages,
        long totalElements,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious
) {
    public static <T> PagedResult<T> of(Page<T> page) {
        return new PagedResult<>(
                page.getContent(),
                page.getNumber() + 1, // Page numbers start from 1 in API, but Spring uses 0-based
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}