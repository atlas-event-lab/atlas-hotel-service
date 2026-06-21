package com.atlas.hotel.exception;

import java.util.UUID;

/**
 * Thrown when an update lowers a room type's {@code totalRooms} below the count already
 * reserved in Inventory → 409 Conflict (feature.md §Capacity-Shrink Validation; ARCH-006).
 */
public class CapacityBelowReservedException extends RuntimeException {

    public CapacityBelowReservedException(UUID roomTypeId, String roomTypeName,
                                          int newTotalRooms, int reservedCount) {
        super("Cannot lower totalRooms to " + newTotalRooms + " for room type '" + roomTypeName
                + "' (" + roomTypeId + "): Inventory has " + reservedCount + " rooms reserved");
    }
}
