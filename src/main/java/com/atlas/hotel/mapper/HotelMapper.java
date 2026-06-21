package com.atlas.hotel.mapper;

import com.atlas.hotel.dto.HotelImageDto;
import com.atlas.hotel.dto.HotelResponse;
import com.atlas.hotel.dto.MoneyResponse;
import com.atlas.hotel.dto.RoomTypeResponse;
import com.atlas.hotel.entity.Amenity;
import com.atlas.hotel.entity.Hotel;
import com.atlas.hotel.entity.HotelImage;
import com.atlas.hotel.entity.Money;
import com.atlas.hotel.entity.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Maps Hotel entities to API response DTOs (mapping is a service-layer concern). */
@Mapper(componentModel = "spring")
public interface HotelMapper {

    @Mapping(target = "hotelId", source = "id")
    HotelResponse toResponse(Hotel hotel);

    @Mapping(target = "roomTypeId", source = "id")
    RoomTypeResponse toRoomTypeResponse(RoomType roomType);

    MoneyResponse toMoneyResponse(Money money);

    HotelImageDto toImageDto(HotelImage image);

    /** Element mapping so {@code List<Amenity>} → {@code List<String>} resolves automatically. */
    default String toAmenityName(Amenity amenity) {
        return amenity.getName();
    }
}
