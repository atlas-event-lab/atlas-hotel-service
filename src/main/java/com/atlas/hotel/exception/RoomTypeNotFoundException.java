package com.atlas.hotel.exception;

import java.util.UUID;

/** Thrown when a room type cannot be found within a hotel (price read → 404). */
public class RoomTypeNotFoundException extends RuntimeException {

    public RoomTypeNotFoundException(UUID hotelId, UUID roomTypeId) {
        super("Room type not found: " + roomTypeId + " in hotel " + hotelId);
    }
}
