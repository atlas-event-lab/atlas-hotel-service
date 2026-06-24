package com.atlas.hotel.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Denormalized room type inside catalog event payloads (hotel-events.yaml RoomType).
 * Carries {@code totalRooms} so Inventory can seed per-room-type availability.
 */
public record RoomTypeEvent(
        @NotNull
        UUID roomTypeId,
        String name,
        int totalRooms,
        int maxOccupancy,

        @Valid
        MoneyEvent pricePerNight
) {}
