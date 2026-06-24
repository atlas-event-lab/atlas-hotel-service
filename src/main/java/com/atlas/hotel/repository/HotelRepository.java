package com.atlas.hotel.repository;

import com.atlas.hotel.entity.Hotel;
import com.atlas.hotel.entity.HotelStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

/** Repository for the hotel catalog. Accesses only local entities (ARCH-003, DB-004). */
public interface HotelRepository extends JpaRepository<Hotel, UUID> {

    /** Business-key uniqueness check for create (services/hotel/service.md). */
    boolean existsByNameAndCity(String name, String city);

    /** Business-key uniqueness check for update, excluding the hotel being updated. */
    boolean existsByNameAndCityAndIdNot(String name, String city, UUID id);

    /** Seeded hotels eligible for bootstrap publishing. */
    List<Hotel> findByStatus(HotelStatus status);

    @Query(
        value = """
          SELECT h.*
          FROM hotels h
          LEFT JOIN outbox o
            ON o.aggregate_id = h.id
           AND o.event_type = 'HOTEL_CREATED'
          WHERE o.id IS NULL
          AND h.status = 'ACTIVE'
          """, nativeQuery = true)
    List<Hotel> findHotelsWithoutCreatedEvent();
}
