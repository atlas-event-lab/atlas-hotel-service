package com.atlas.hotel.service;

import com.atlas.hotel.client.InventoryClient;
import com.atlas.hotel.client.dto.AvailabilityResponse;
import com.atlas.hotel.dto.CreateHotelRequest;
import com.atlas.hotel.dto.HotelImageDto;
import com.atlas.hotel.dto.HotelListResponse;
import com.atlas.hotel.dto.HotelResponse;
import com.atlas.hotel.dto.MoneyRequest;
import com.atlas.hotel.dto.MoneyResponse;
import com.atlas.hotel.dto.RoomImageDto;
import com.atlas.hotel.dto.RoomTypeInput;
import com.atlas.hotel.dto.RoomTypePriceResponse;
import com.atlas.hotel.dto.UpdateHotelRequest;
import com.atlas.hotel.entity.Amenity;
import com.atlas.hotel.entity.Hotel;
import com.atlas.hotel.entity.HotelImage;
import com.atlas.hotel.entity.HotelStatus;
import com.atlas.hotel.entity.Money;
import com.atlas.hotel.entity.RoomImage;
import com.atlas.hotel.entity.RoomType;
import com.atlas.hotel.event.HotelDeletedPayload;
import com.atlas.hotel.event.HotelEventPayloadFactory;
import com.atlas.hotel.shared.messaging.EventType;
import com.atlas.hotel.exception.CapacityBelowReservedException;
import com.atlas.hotel.exception.DuplicateHotelException;
import com.atlas.hotel.exception.HotelNotFoundException;
import com.atlas.hotel.exception.InvalidHotelException;
import com.atlas.hotel.exception.InventoryUnavailableException;
import com.atlas.hotel.exception.RoomTypeNotFoundException;
import com.atlas.hotel.mapper.HotelMapper;
import com.atlas.hotel.messaging.OutboxEventWriter;
import com.atlas.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Hotel catalog service. Validates input, persists the catalog aggregate and its event in one
 * transaction (Transactional Outbox, EVT-009/EVT-010), and enforces the per-room-type
 * capacity-shrink rule against Inventory before lowering capacity (ARCH-006). All entity-to-DTO
 * mapping happens here (coding-standards §Layer Responsibilities).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private static final String HOTEL_RESOURCE_TYPE = "HOTEL";

    private final HotelRepository hotelRepository;
    private final InventoryClient inventoryClient;
    private final OutboxEventWriter outboxEventWriter;
    private final HotelEventPayloadFactory payloadFactory;
    private final HotelMapper hotelMapper;

    @Override
    @Transactional
    public HotelResponse createHotel(CreateHotelRequest request) {
        validateRoomTypeNamesUnique(request.roomTypes());

        if (hotelRepository.existsByNameAndCity(request.name(), request.city())) {
            throw new DuplicateHotelException(request.name(), request.city());
        }

        Hotel hotel = new Hotel(
                UUID.randomUUID(),
                request.name(),
                request.city(),
                request.country(),
                request.rating(),
                HotelStatus.ACTIVE);
        toRoomTypeEntities(request.roomTypes()).forEach(hotel::addRoomType);
        hotel.replaceAmenities(toAmenityEntities(request.amenities()));
        hotel.replaceImages(toImageEntities(request.images()));

        Hotel saved = hotelRepository.save(hotel);
        outboxEventWriter.write(saved.getId(), EventType.HOTEL_CREATED, payloadFactory.toCatalogPayload(saved));

        log.info("Hotel created: hotelId={}, name={}, city={}, roomTypes={}",
                saved.getId(), saved.getName(), saved.getCity(), saved.getRoomTypes().size());

        return hotelMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public HotelResponse updateHotel(UUID hotelId, UpdateHotelRequest request) {
        validateRoomTypeNamesUnique(request.roomTypes());

        Hotel hotel = findHotel(hotelId);

        if (hotelRepository.existsByNameAndCityAndIdNot(request.name(), request.city(), hotelId)) {
            throw new DuplicateHotelException(request.name(), request.city());
        }

        // Per-room-type capacity-shrink guard — run all checks before any mutation (feature.md).
        assertCapacityNotBelowReserved(hotel, request.roomTypes());

        hotel.update(request.name(), request.city(), request.country(), request.rating());
        hotel.reconcileRoomTypes(toRoomTypeEntities(request.roomTypes()));
        hotel.replaceAmenities(toAmenityEntities(request.amenities()));
        hotel.replaceImages(toImageEntities(request.images()));

        outboxEventWriter.write(hotel.getId(), EventType.HOTEL_UPDATED, payloadFactory.toCatalogPayload(hotel));

        log.info("Hotel updated: hotelId={}, name={}, roomTypes={}",
                hotel.getId(), hotel.getName(), hotel.getRoomTypes().size());

        return hotelMapper.toResponse(hotel);
    }

    @Override
    @Transactional
    public void withdrawHotel(UUID hotelId) {
        Hotel hotel = findHotel(hotelId);
        hotel.withdraw();
        outboxEventWriter.write(hotel.getId(), EventType.HOTEL_DELETED, new HotelDeletedPayload(hotel.getId()));

        log.info("Hotel withdrawn: hotelId={}", hotel.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public HotelResponse getHotel(UUID hotelId) {
        return hotelMapper.toResponse(findHotel(hotelId));
    }

    @Override
    @Transactional(readOnly = true)
    public RoomTypePriceResponse getRoomTypePrice(UUID hotelId, UUID roomTypeId) {
        log.info("Get RoomType price for hotelId={}, roomTypeId={}", hotelId, roomTypeId);
        Hotel hotel = findHotel(hotelId);
        RoomType roomType = hotel.getRoomTypes().stream()
                .filter(rt -> rt.getId().equals(roomTypeId))
                .findFirst()
                .orElseThrow(() -> new RoomTypeNotFoundException(hotelId, roomTypeId));
        return new RoomTypePriceResponse(
                hotel.getId(),
                roomType.getId(),
                new MoneyResponse(roomType.getPricePerNight().getAmount(), roomType.getPricePerNight().getCurrency()),
                hotel.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public HotelListResponse listHotels(Pageable pageable) {
        Page<HotelResponse> page = hotelRepository.findAll(pageable).map(hotelMapper::toResponse);
        return HotelListResponse.from(page);
    }

    // -------------------------------------------------------------------------
    // Capacity-shrink (per room type, matched by name)
    // -------------------------------------------------------------------------

    private void assertCapacityNotBelowReserved(Hotel hotel, List<RoomTypeInput> requested) {
        Map<String, RoomType> existingByName = hotel.getRoomTypes().stream()
                .collect(Collectors.toMap(RoomType::getName, Function.identity()));

        for (RoomTypeInput input : requested) {
            RoomType existing = existingByName.get(input.name());
            if (existing == null || input.totalRooms() >= existing.getTotalRooms()) {
                continue; // new room type, or capacity not decreasing → no Inventory check
            }
            int reservedCount = readReservedCount(existing.getId());
            if (input.totalRooms() < reservedCount) {
                throw new CapacityBelowReservedException(
                        existing.getId(), existing.getName(), input.totalRooms(), reservedCount);
            }
        }
    }

    private int readReservedCount(UUID roomTypeId) {
        try {
            AvailabilityResponse availability =
                    inventoryClient.getAvailability(HOTEL_RESOURCE_TYPE, roomTypeId);
            return availability.reservedCount();
        } catch (Exception e) {
            // A transient failure is a failed precondition — never silently skip the check.
            throw new InventoryUnavailableException(roomTypeId, e);
        }
    }

    // -------------------------------------------------------------------------
    // Validation & builders
    // -------------------------------------------------------------------------

    private void validateRoomTypeNamesUnique(List<RoomTypeInput> roomTypes) {
        Set<String> names = new HashSet<>();
        for (RoomTypeInput rt : roomTypes) {
            if (!names.add(rt.name())) {
                throw new InvalidHotelException("Duplicate room type name in request: " + rt.name());
            }
        }
    }

    private List<RoomType> toRoomTypeEntities(List<RoomTypeInput> inputs) {
        return inputs.stream().map(roomTypeInput -> {
                var roomType = new RoomType(
                    UUID.randomUUID(),
                    roomTypeInput.name(),
                    roomTypeInput.totalRooms(),
                    roomTypeInput.maxOccupancy(),
                    toMoney(roomTypeInput.pricePerNight())
                );
                roomType.replaceImages(toRoomImageEntities(roomTypeInput.images()));
                return roomType;
            }
        ).toList();
    }

    private List<Amenity> toAmenityEntities(List<String> amenities) {
        if (amenities == null) {
            return List.of();
        }
        return amenities.stream()
                .map(name -> new Amenity(UUID.randomUUID(), name))
                .toList();
    }

    private List<HotelImage> toImageEntities(List<HotelImageDto> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream()
                .map(img -> new HotelImage(UUID.randomUUID(), img.url(), img.caption()))
                .toList();
    }

    private List<RoomImage> toRoomImageEntities(List<RoomImageDto> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream()
            .map(img -> new RoomImage(UUID.randomUUID(), img.url(), img.caption()))
            .toList();
    }

    private Money toMoney(MoneyRequest money) {
        return new Money(money.amount(), money.currency());
    }

    private Hotel findHotel(UUID hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));
    }
}
