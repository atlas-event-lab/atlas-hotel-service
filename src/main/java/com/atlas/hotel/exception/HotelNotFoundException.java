package com.atlas.hotel.exception;

import java.util.UUID;

/** Thrown when a hotel cannot be found by id (get/update/withdraw → 404). */
public class HotelNotFoundException extends RuntimeException {

    public HotelNotFoundException(UUID hotelId) {
        super("Hotel not found: " + hotelId);
    }
}
