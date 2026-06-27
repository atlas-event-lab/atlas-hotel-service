package com.atlas.hotel.dto;

import com.atlas.hotel.entity.HotelStatus;

import java.util.UUID;

/** Minimal room-type price read for service callers (hotel.yaml RoomTypePriceResponse, ADR-0005). */
public record RoomTypePriceResponse(
        UUID hotelId,
        UUID roomTypeId,
        MoneyResponse pricePerNight,
        HotelStatus status
) {}
