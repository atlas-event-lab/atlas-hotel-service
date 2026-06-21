package com.atlas.hotel.mapper;

import com.atlas.hotel.dto.HotelResponse;
import com.atlas.hotel.entity.Amenity;
import com.atlas.hotel.entity.Hotel;
import com.atlas.hotel.entity.HotelImage;
import com.atlas.hotel.hotel.mapper.HotelMapperImpl;
import com.atlas.hotel.support.HotelTestData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HotelMapperTest {

    private final HotelMapper mapper = new HotelMapperImpl();

    @Test
    void toResponse_maps_id_roomTypes_amenities_and_images() {
        Hotel hotel = HotelTestData.aHotel();
        hotel.replaceAmenities(List.of(new Amenity(UUID.randomUUID(), "WiFi")));
        hotel.replaceImages(List.of(new HotelImage(UUID.randomUUID(), "https://cdn.atlas.local/h1.jpg", "Lobby")));

        HotelResponse response = mapper.toResponse(hotel);

        assertThat(response.hotelId()).isEqualTo(HotelTestData.HOTEL_ID);
        assertThat(response.roomTypes()).hasSize(1);
        assertThat(response.roomTypes().get(0).roomTypeId()).isEqualTo(HotelTestData.STANDARD_ROOM_ID);
        assertThat(response.roomTypes().get(0).pricePerNight().currency()).isEqualTo("USD");
        assertThat(response.amenities()).containsExactly("WiFi");
        assertThat(response.images().get(0).url()).isEqualTo("https://cdn.atlas.local/h1.jpg");
    }
}
