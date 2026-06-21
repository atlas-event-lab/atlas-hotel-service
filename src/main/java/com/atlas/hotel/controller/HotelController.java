package com.atlas.hotel.controller;

import com.atlas.hotel.dto.CreateHotelRequest;
import com.atlas.hotel.dto.HotelListResponse;
import com.atlas.hotel.dto.HotelResponse;
import com.atlas.hotel.dto.UpdateHotelRequest;
import com.atlas.hotel.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for the admin hotel catalog (hotel.yaml).
 * Contains no business logic; delegates entirely to {@link HotelService}, which also enforces
 * RBAC and performs entity-to-DTO mapping (coding-standards §Layer Responsibilities).
 */
@RestController
@RequestMapping("/admin/api/v1/hotels")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class HotelController {

    private final HotelService hotelService;

    /** POST /api/v1/hotels — creates a hotel with its room types (hotel.yaml operationId: createHotel). */
    @PostMapping
    public ResponseEntity<HotelResponse> createHotel(@RequestBody @Valid CreateHotelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(request));
    }

    /** GET /api/v1/hotels — paginated list (hotel.yaml operationId: listHotels). */
    @GetMapping
    public ResponseEntity<HotelListResponse> listHotels(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(hotelService.listHotels(pageable));
    }

    /** GET /api/v1/hotels/{hotelId} — retrieves a hotel (hotel.yaml operationId: getHotel). */
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelResponse> getHotel(@PathVariable UUID hotelId) {
        return ResponseEntity.ok(hotelService.getHotel(hotelId));
    }

    /** PUT /api/v1/hotels/{hotelId} — updates a hotel (hotel.yaml operationId: updateHotel). */
    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelResponse> updateHotel(
            @PathVariable UUID hotelId,
            @RequestBody @Valid UpdateHotelRequest request) {
        return ResponseEntity.ok(hotelService.updateHotel(hotelId, request));
    }

    /** DELETE /api/v1/hotels/{hotelId} — withdraws a hotel (hotel.yaml operationId: withdrawHotel). */
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> withdrawHotel(@PathVariable UUID hotelId) {
        hotelService.withdrawHotel(hotelId);
        return ResponseEntity.noContent().build();
    }
}
