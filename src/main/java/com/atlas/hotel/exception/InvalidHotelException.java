package com.atlas.hotel.exception;

/**
 * Thrown when a hotel request fails a business validation rule that bean validation cannot
 * express (e.g. duplicate room-type names within the request) → 400 Bad Request (API-004).
 */
public class InvalidHotelException extends RuntimeException {

    public InvalidHotelException(String message) {
        super(message);
    }
}
