package com.atlas.hotel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request body for PUT /hotels/{hotelId} (hotel.yaml UpdateHotelRequest). Replaces hotel /
 * room types / price / capacity. Room types are reconciled by {@code name} (resolved Open
 * Question); a shrinking room type is validated against Inventory before persisting (API-004).
 */
public record UpdateHotelRequest(

        @NotBlank
        String name,

        @NotBlank
        String city,

        @NotBlank
        String country,

        @NotNull
        @Min(1)
        @Max(5)
        Integer rating,

        @NotNull
        @Size(min = 1)
        @Valid
        List<RoomTypeInput> roomTypes,

        List<String> amenities,

        @Valid
        List<HotelImageDto> images
) {}
