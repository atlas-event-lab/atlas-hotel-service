package com.atlas.hotel.messaging;

import com.atlas.hotel.entity.OutboxEvent;
import com.atlas.hotel.repository.OutboxRepository;
import com.atlas.hotel.shared.messaging.EventEnvelope;
import com.atlas.hotel.shared.messaging.EventType;
import com.atlas.hotel.shared.web.CorrelationIdFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Writes catalog events to the Transactional Outbox (EVT-009).
 * <p>
 * Called from inside a {@code @Transactional} Service method so the outbox row is committed
 * atomically with the hotel state change — no Kafka call happens here, avoiding the dual-write
 * (coding-standards §Outbox & Event Publishing). The {@code OutboxRelay} publishes the row
 * afterwards. Catalog events are not part of a saga, so {@code sagaId} is null; the envelope
 * still carries traceId/correlationId (OBS-002).
 */
@Component
@RequiredArgsConstructor
public class OutboxEventWriter {

    private static final String PRODUCER = "hotel-service";
    private static final String AGGREGATE_TYPE = "Hotel";
    private static final int EVENT_VERSION = 1;

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Builds the full event envelope (message-envelope.md) and stores it as a PENDING outbox row.
     * Partition key for publication is {@code aggregateId} = hotelId (partitioning.md).
     *
     * @param aggregateId the Hotel id (also the Kafka partition key)
     * @param eventType   produced event type, e.g. {@code HOTEL_CREATED}
     * @param payload     the business payload (never null, never carries metadata)
     */
    public <T> void write(UUID aggregateId, EventType eventType, T payload) {
        EventEnvelope<T> envelope = new EventEnvelope<>(
                UUID.randomUUID(),
                eventType.name(),
                EVENT_VERSION,
                Instant.now(),
                resolveMdc(CorrelationIdFilter.TRACE_ID_MDC_KEY),
                resolveMdc(CorrelationIdFilter.MDC_KEY),
                null,
                PRODUCER,
                payload);

        outboxRepository.save(
            new OutboxEvent(
                UUID.randomUUID(),
                AGGREGATE_TYPE,
                aggregateId,
                eventType,
                EVENT_VERSION,
                serialize(envelope))
        );
    }

    private String serialize(EventEnvelope<?> envelope) {
        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to serialize event envelope for outbox: eventType=" + envelope.eventType(), e);
        }
    }

    /** Reads a value from MDC (set by {@link CorrelationIdFilter}), falls back to a new UUID. */
    private String resolveMdc(String key) {
        String value = MDC.get(key);
        return (value != null && !value.isBlank()) ? value : UUID.randomUUID().toString();
    }
}
