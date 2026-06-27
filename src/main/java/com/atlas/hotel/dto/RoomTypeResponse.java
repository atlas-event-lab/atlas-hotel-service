package com.atlas.hotel.dto;

import java.util.List;
import java.util.UUID;

/** Room type in API responses (hotel.yaml RoomType = roomTypeId + RoomTypeInput fields). */
public record RoomTypeResponse(
        UUID roomTypeId,
        String name,
        int totalRooms,
        int maxOccupancy,
        MoneyResponse pricePerNight,
        List<RoomImageDto> images
) {}
