package com.atlas.hotel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Request body for POST /hotels (hotel.yaml CreateHotelRequest). */
public record CreateHotelRequest(

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
