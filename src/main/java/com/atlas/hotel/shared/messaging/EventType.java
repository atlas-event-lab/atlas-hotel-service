package com.atlas.hotel.shared.messaging;

/**
 * Produced event types (hotel-events.yaml message names). Stored on each outbox row and used by
 * {@code OutboxRelay} to resolve the destination topic. Names are past-tense, completed facts
 * (events.md §Naming Rules).
 */
public enum EventType {
    HOTEL_CREATED,
    HOTEL_UPDATED,
    HOTEL_DELETED
}
