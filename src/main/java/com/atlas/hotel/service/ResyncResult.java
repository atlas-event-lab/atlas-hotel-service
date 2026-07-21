package com.atlas.hotel.service;

/**
 * Summary of a catalog resync (ADR-0026): how many current-state catalog events were re-emitted
 * through the outbox for a read-model rebuild.
 *
 * @param active    ACTIVE hotels re-emitted as {@code HOTEL_CREATED} (upsert)
 * @param withdrawn WITHDRAWN hotels re-emitted as {@code HOTEL_DELETED}
 */
public record ResyncResult(int active, int withdrawn) {
    public int total() {
        return active + withdrawn;
    }
}
