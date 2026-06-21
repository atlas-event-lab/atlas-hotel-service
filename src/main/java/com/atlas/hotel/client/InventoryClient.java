package com.atlas.hotel.client;

import com.atlas.hotel.client.dto.AvailabilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Read-only REST client for the Inventory Service (ARCH-003, ARCH-006). Used solely to read the
 * reserved room count for a room type when an update lowers its capacity (capacity-shrink
 * validation, feature.md). Hotel never reads Inventory's database.
 */
@FeignClient(
    name = "inventory-service",
    url = "${clients.inventory.base-url}"
)
public interface InventoryClient {

    /** Calls Inventory {@code GET /inventory/{resourceType}/{resourceId}} ({@code resourceId = roomTypeId}). */
    @GetMapping("/api/v1/inventory/{resourceType}/{resourceId}")
    AvailabilityResponse getAvailability(@PathVariable String resourceType,
                                         @PathVariable UUID resourceId);
}
