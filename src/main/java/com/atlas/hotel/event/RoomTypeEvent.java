package com.atlas.hotel.event;

import java.util.UUID;

/**
 * Denormalized room type inside catalog event payloads (hotel-events.yaml RoomType).
 * Carries {@code totalRooms} so Inventory can seed per-room-type availability.
 */
public record RoomTypeEvent(
        UUID roomTypeId,
        String name,
        int totalRooms,
        int maxOccupancy,
        MoneyEvent pricePerNight
) {}
