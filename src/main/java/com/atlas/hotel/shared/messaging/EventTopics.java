package com.atlas.hotel.shared.messaging;

/**
 * Kafka topic name constants (topics.md, naming: domain.entity.event).
 * Topics prefixed hotel.* are owned by Hotel Service.
 * Topic names are immutable — never rename or reuse a topic.
 */
public final class EventTopics {

    // ── Hotel Service produces ────────────────────────────────────────────────
    public static final String HOTEL_CREATED = "hotel.created";
    public static final String HOTEL_UPDATED = "hotel.updated";
    public static final String HOTEL_DELETED = "hotel.deleted";

    private EventTopics() {}
}
