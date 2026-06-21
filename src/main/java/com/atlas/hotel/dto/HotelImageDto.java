package com.atlas.hotel.dto;

import jakarta.validation.constraints.NotBlank;

/** Hotel image in requests and responses (hotel.yaml HotelImage). */
public record HotelImageDto(

        @NotBlank
        String url,

        String caption
) {}
