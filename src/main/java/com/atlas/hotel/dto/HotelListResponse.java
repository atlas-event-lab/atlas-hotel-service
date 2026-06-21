package com.atlas.hotel.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Paginated hotels (hotel.yaml HotelListResponse = PageResponse + content[]).
 * Built from a Spring Data {@link Page} (API-006).
 */
public record HotelListResponse(
        List<HotelResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static HotelListResponse from(Page<HotelResponse> page) {
        return new HotelListResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
