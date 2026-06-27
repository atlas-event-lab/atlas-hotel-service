package com.atlas.hotel.service;

import com.atlas.hotel.dto.CreateHotelRequest;
import com.atlas.hotel.dto.HotelListResponse;
import com.atlas.hotel.dto.HotelResponse;
import com.atlas.hotel.dto.RoomTypePriceResponse;
import com.atlas.hotel.dto.UpdateHotelRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

/**
 * Hotel catalog operations. Admin writes require RBAC {@code ADMIN} (SEC-004) via
 * {@code @PreAuthorize}. The room-type price read is available to any authenticated
 * caller (ADR-0005).
 */
public interface HotelService {

    @PreAuthorize("hasRole('ADMIN')")
    HotelResponse createHotel(CreateHotelRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    HotelResponse updateHotel(UUID hotelId, UpdateHotelRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    void withdrawHotel(UUID hotelId);

    @PreAuthorize("hasRole('ADMIN')")
    HotelResponse getHotel(UUID hotelId);

    @PreAuthorize("hasRole('ADMIN')")
    HotelListResponse listHotels(Pageable pageable);

    RoomTypePriceResponse getRoomTypePrice(UUID hotelId, UUID roomTypeId);
}
