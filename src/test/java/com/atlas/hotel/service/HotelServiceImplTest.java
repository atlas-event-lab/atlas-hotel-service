package com.atlas.hotel.service;

import com.atlas.hotel.client.InventoryClient;
import com.atlas.hotel.client.dto.AvailabilityResponse;
import com.atlas.hotel.dto.CreateHotelRequest;
import com.atlas.hotel.entity.Hotel;
import com.atlas.hotel.entity.HotelStatus;
import com.atlas.hotel.entity.RoomType;
import com.atlas.hotel.event.HotelCatalogPayload;
import com.atlas.hotel.event.HotelEventPayloadFactory;
import com.atlas.hotel.exception.CapacityBelowReservedException;
import com.atlas.hotel.exception.DuplicateHotelException;
import com.atlas.hotel.exception.HotelNotFoundException;
import com.atlas.hotel.exception.InvalidHotelException;
import com.atlas.hotel.exception.InventoryUnavailableException;
import com.atlas.hotel.mapper.HotelMapper;
import com.atlas.hotel.messaging.OutboxEventWriter;
import com.atlas.hotel.repository.HotelRepository;
import com.atlas.hotel.support.HotelTestData;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelServiceImplTest {

    @Mock HotelRepository hotelRepository;
    @Mock InventoryClient inventoryClient;
    @Mock OutboxEventWriter outboxEventWriter;
    @Mock HotelEventPayloadFactory payloadFactory;
    @Mock HotelMapper hotelMapper;

    @InjectMocks
    HotelServiceImpl service;

    @BeforeEach
    void stubMappers() {
        when(payloadFactory.toCatalogPayload(any())).thenReturn(mock_payload());
        when(hotelMapper.toResponse(any())).thenReturn(HotelTestData.aHotelResponse());
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    void createHotel_valid_persists_and_writes_HotelCreated() {
        when(hotelRepository.existsByNameAndCity(any(), any())).thenReturn(false);
        when(hotelRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.createHotel(HotelTestData.aCreateHotelRequest());

        verify(hotelRepository).save(any(Hotel.class));
        verify(outboxEventWriter).write(any(UUID.class), eq("HotelCreated"), any());
    }

    @Test
    void createHotel_duplicateNameCity_throwsConflict_and_writes_no_event() {
        when(hotelRepository.existsByNameAndCity(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.createHotel(HotelTestData.aCreateHotelRequest()))
                .isInstanceOf(DuplicateHotelException.class);

        verify(hotelRepository, never()).save(any());
        verifyNoInteractions(outboxEventWriter);
    }

    @Test
    void createHotel_duplicateRoomTypeNames_throwsInvalid() {
        CreateHotelRequest invalid = new CreateHotelRequest(
                HotelTestData.NAME, HotelTestData.CITY, HotelTestData.COUNTRY, HotelTestData.RATING,
                List.of(HotelTestData.aRoomType("Standard", 10), HotelTestData.aRoomType("Standard", 20)),
                List.of(), List.of());

        assertThatThrownBy(() -> service.createHotel(invalid))
                .isInstanceOf(InvalidHotelException.class);

        verify(hotelRepository, never()).save(any());
    }

    // ── update / per-room-type capacity-shrink ─────────────────────────────────

    @Test
    void updateHotel_notFound_throws404() {
        when(hotelRepository.findById(HotelTestData.HOTEL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateHotel(
                HotelTestData.HOTEL_ID, HotelTestData.anUpdateHotelRequest(100)))
                .isInstanceOf(HotelNotFoundException.class);
    }

    @Test
    void updateHotel_roomTypeShrinkBelowReserved_throwsConflict_and_writes_no_event() {
        when(hotelRepository.findById(HotelTestData.HOTEL_ID)).thenReturn(Optional.of(HotelTestData.aHotel()));
        when(inventoryClient.getAvailability(eq("HOTEL"), eq(HotelTestData.STANDARD_ROOM_ID)))
                .thenReturn(new AvailabilityResponse("HOTEL", HotelTestData.STANDARD_ROOM_ID, 100, 150, 0, "ACTIVE"));

        assertThatThrownBy(() -> service.updateHotel(
                HotelTestData.HOTEL_ID, HotelTestData.anUpdateHotelRequest(50)))
                .isInstanceOf(CapacityBelowReservedException.class);

        verify(outboxEventWriter, never()).write(any(), any(), any());
    }

    @Test
    void updateHotel_roomTypeShrinkAtOrAboveReserved_proceeds_and_writes_HotelUpdated() {
        when(hotelRepository.findById(HotelTestData.HOTEL_ID)).thenReturn(Optional.of(HotelTestData.aHotel()));
        when(hotelRepository.existsByNameAndCityAndIdNot(any(), any(), any())).thenReturn(false);
        when(inventoryClient.getAvailability(eq("HOTEL"), eq(HotelTestData.STANDARD_ROOM_ID)))
                .thenReturn(new AvailabilityResponse("HOTEL", HotelTestData.STANDARD_ROOM_ID, 100, 30, 70, "ACTIVE"));

        service.updateHotel(HotelTestData.HOTEL_ID, HotelTestData.anUpdateHotelRequest(50));

        verify(outboxEventWriter).write(eq(HotelTestData.HOTEL_ID), eq("HotelUpdated"), any());
    }

    @Test
    void updateHotel_capacityIncrease_skips_inventory_check() {
        when(hotelRepository.findById(HotelTestData.HOTEL_ID)).thenReturn(Optional.of(HotelTestData.aHotel()));
        when(hotelRepository.existsByNameAndCityAndIdNot(any(), any(), any())).thenReturn(false);

        service.updateHotel(HotelTestData.HOTEL_ID, HotelTestData.anUpdateHotelRequest(150));

        verifyNoInteractions(inventoryClient);
        verify(outboxEventWriter).write(eq(HotelTestData.HOTEL_ID), eq("HotelUpdated"), any());
    }

    @Test
    void updateHotel_newRoomTypeName_skips_inventory_check() {
        when(hotelRepository.findById(HotelTestData.HOTEL_ID)).thenReturn(Optional.of(HotelTestData.aHotel()));
        when(hotelRepository.existsByNameAndCityAndIdNot(any(), any(), any())).thenReturn(false);

        service.updateHotel(HotelTestData.HOTEL_ID,
                HotelTestData.anUpdateHotelRequest(List.of(HotelTestData.aRoomType("Suite", 5))));

        verifyNoInteractions(inventoryClient);
        verify(outboxEventWriter).write(eq(HotelTestData.HOTEL_ID), eq("HotelUpdated"), any());
    }

    @Test
    void updateHotel_inventoryUnreachable_throwsUnavailable_and_writes_no_event() {
        when(hotelRepository.findById(HotelTestData.HOTEL_ID)).thenReturn(Optional.of(HotelTestData.aHotel()));
        when(inventoryClient.getAvailability(any(), any())).thenThrow(mock_feignException());

        assertThatThrownBy(() -> service.updateHotel(
                HotelTestData.HOTEL_ID, HotelTestData.anUpdateHotelRequest(50)))
                .isInstanceOf(InventoryUnavailableException.class);

        verify(outboxEventWriter, never()).write(any(), any(), any());
    }

    @Test
    void updateHotel_reconcileByName_keepsExistingRoomTypeId_andAddsNew() {
        Hotel hotel = HotelTestData.aHotel();
        when(hotelRepository.findById(HotelTestData.HOTEL_ID)).thenReturn(Optional.of(hotel));
        when(hotelRepository.existsByNameAndCityAndIdNot(any(), any(), any())).thenReturn(false);

        service.updateHotel(HotelTestData.HOTEL_ID, HotelTestData.anUpdateHotelRequest(
                List.of(HotelTestData.aRoomType("Standard", 100), HotelTestData.aRoomType("Deluxe", 10))));

        RoomType standard = hotel.getRoomTypes().stream()
                .filter(rt -> rt.getName().equals("Standard")).findFirst().orElseThrow();
        RoomType deluxe = hotel.getRoomTypes().stream()
                .filter(rt -> rt.getName().equals("Deluxe")).findFirst().orElseThrow();

        assertThat(standard.getId()).isEqualTo(HotelTestData.STANDARD_ROOM_ID);
        assertThat(deluxe.getId()).isNotEqualTo(HotelTestData.STANDARD_ROOM_ID);
        assertThat(hotel.getRoomTypes()).hasSize(2);
    }

    // ── withdraw ───────────────────────────────────────────────────────────────

    @Test
    void withdrawHotel_softDeactivates_and_writes_HotelDeleted() {
        Hotel hotel = HotelTestData.aHotel();
        when(hotelRepository.findById(HotelTestData.HOTEL_ID)).thenReturn(Optional.of(hotel));

        service.withdrawHotel(HotelTestData.HOTEL_ID);

        assertThat(hotel.getStatus()).isEqualTo(HotelStatus.WITHDRAWN);
        verify(outboxEventWriter).write(eq(HotelTestData.HOTEL_ID), eq("HotelDeleted"), any());
    }

    @Test
    void getHotel_notFound_throws404() {
        when(hotelRepository.findById(HotelTestData.HOTEL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getHotel(HotelTestData.HOTEL_ID))
                .isInstanceOf(HotelNotFoundException.class);
    }

    private static HotelCatalogPayload mock_payload() {
        return new HotelCatalogPayload(
                HotelTestData.HOTEL_ID, HotelTestData.NAME, HotelTestData.CITY, HotelTestData.COUNTRY,
                HotelTestData.RATING, List.of(), List.of(), List.of());
    }

    private static FeignException mock_feignException() {
        return new FeignException.ServiceUnavailable(
                "inventory down", feign.Request.create(
                        feign.Request.HttpMethod.GET, "/api/v1/inventory/HOTEL/x",
                        java.util.Map.of(), null, null, null), null, null);
    }
}
