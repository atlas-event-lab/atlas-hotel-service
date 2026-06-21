package com.atlas.hotel.event;

import java.util.UUID;

/** Payload for {@code HotelDeleted} (hotel-events.yaml HotelDeletedPayload). */
public record HotelDeletedPayload(UUID hotelId) {}
