package com.atlas.hotel.dto;

import com.atlas.hotel.entity.HotelStatus;

import java.util.List;
import java.util.UUID;

/** Hotel API response (hotel.yaml HotelResponse). */
public record HotelResponse(
        UUID hotelId,
        String name,
        String city,
        String country,
        int rating,
        HotelStatus status,
        List<RoomTypeResponse> roomTypes,
        List<String> amenities,
        List<HotelImageDto> images
) {}
