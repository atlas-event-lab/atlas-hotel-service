package com.atlas.hotel.event;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Money representation inside catalog event payloads (hotel-events.yaml Money). */
public record MoneyEvent(
        @NotNull
        BigDecimal amount,
        String currency
) {}
