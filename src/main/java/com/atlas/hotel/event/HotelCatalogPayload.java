package com.atlas.hotel.event;

import java.util.List;
import java.util.UUID;

/**
 * Payload for {@code HotelCreated} / {@code HotelUpdated} (hotel-events.yaml HotelCatalogPayload).
 * Denormalized (descriptive fields + room types with {@code totalRooms} + {@code pricePerNight})
 * so Search needs no cross-service join (ARCH-004); carries per-room-type {@code totalRooms} so
 * Inventory can seed room availability. Never carries live availability (data ownership).
 */
public record HotelCatalogPayload(
        UUID hotelId,
        String name,
        String city,
        String country,
        int rating,
        List<RoomTypeEvent> roomTypes,
        List<String> amenities,
        List<String> images
) {}
