package com.atlas.hotel.entity;

/**
 * Lifecycle status of a catalog {@link Hotel} (services/hotel/service.md).
 * A withdrawn hotel is soft-deactivated (no hard delete) and no longer bookable.
 */
public enum HotelStatus {

    /** Bookable; present in the catalog. */
    ACTIVE,

    /** Withdrawn from sale (soft delete); no longer bookable. */
    WITHDRAWN
}
