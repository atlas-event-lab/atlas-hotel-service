package com.atlas.hotel.event;

import java.math.BigDecimal;

/** Money representation inside catalog event payloads (hotel-events.yaml Money). */
public record MoneyEvent(BigDecimal amount, String currency) {}
