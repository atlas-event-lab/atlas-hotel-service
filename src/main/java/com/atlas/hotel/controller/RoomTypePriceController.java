package com.atlas.hotel.controller;

import com.atlas.hotel.dto.RoomTypePriceResponse;
import com.atlas.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Service-readable room-type price endpoint (ADR-0005, hotel.yaml operationId: getRoomTypePrice).
 * Available to any authenticated caller; no ADMIN role required.
 */
@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class RoomTypePriceController {

    private final HotelService hotelService;

    /** GET /api/v1/hotels/{hotelId}/room-types/{roomTypeId}/price — returns price per night and status. */
    @GetMapping("/{hotelId}/room-types/{roomTypeId}/price")
    public ResponseEntity<RoomTypePriceResponse> getRoomTypePrice(
            @PathVariable UUID hotelId,
            @PathVariable UUID roomTypeId) {
        return ResponseEntity.ok(hotelService.getRoomTypePrice(hotelId, roomTypeId));
    }
}
