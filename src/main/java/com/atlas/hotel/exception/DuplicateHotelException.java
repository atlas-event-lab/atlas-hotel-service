package com.atlas.hotel.exception;

/**
 * Thrown when a create would violate the hotel uniqueness key
 * ({@code name} + {@code city}) → 409 Conflict (services/hotel/service.md §Hotel uniqueness key).
 */
public class DuplicateHotelException extends RuntimeException {

    public DuplicateHotelException(String name, String city) {
        super("Hotel already exists for name=" + name + " city=" + city);
    }
}
