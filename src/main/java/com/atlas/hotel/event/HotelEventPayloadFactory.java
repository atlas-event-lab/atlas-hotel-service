package com.atlas.hotel.event;

import com.atlas.hotel.entity.Amenity;
import com.atlas.hotel.entity.Hotel;
import com.atlas.hotel.entity.HotelImage;
import com.atlas.hotel.entity.RoomType;
import org.springframework.stereotype.Component;

/**
 * Builds denormalized catalog event payloads (ARCH-004, EVT-006) straight from the aggregate —
 * unlike Flight there are no cross-service reference lookups. Images are flattened to their URL
 * only (hotel-events.yaml carries image URIs, not captions). Shared by the admin service and the
 * bootstrap publisher so the payload shape is identical.
 */
@Component
public class HotelEventPayloadFactory {

    public HotelCatalogPayload toCatalogPayload(Hotel hotel) {
        return new HotelCatalogPayload(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getCountry(),
                hotel.getRating(),
                hotel.getRoomTypes().stream().map(this::toRoomTypeEvent).toList(),
                hotel.getAmenities().stream().map(Amenity::getName).toList(),
                hotel.getImages().stream().map(HotelImage::getUrl).toList());
    }

    private RoomTypeEvent toRoomTypeEvent(RoomType roomType) {
        return new RoomTypeEvent(
                roomType.getId(),
                roomType.getName(),
                roomType.getTotalRooms(),
                roomType.getMaxOccupancy(),
                new MoneyEvent(roomType.getPricePerNight().getAmount(), roomType.getPricePerNight().getCurrency()));
    }
}
