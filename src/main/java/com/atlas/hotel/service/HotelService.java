package com.atlas.hotel.service;

import com.atlas.hotel.dto.CreateHotelRequest;
import com.atlas.hotel.dto.HotelListResponse;
import com.atlas.hotel.dto.HotelResponse;
import com.atlas.hotel.dto.UpdateHotelRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

/**
 * Admin catalog operations. Authorization (RBAC {@code ADMIN}) is enforced here, in the business
 * service layer (SEC-004), via {@code @PreAuthorize}.
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
}
