package com.atlas.hotel.support;

import com.atlas.hotel.dto.CreateHotelRequest;
import com.atlas.hotel.dto.HotelImageDto;
import com.atlas.hotel.dto.HotelResponse;
import com.atlas.hotel.dto.MoneyRequest;
import com.atlas.hotel.dto.MoneyResponse;
import com.atlas.hotel.dto.RoomTypeInput;
import com.atlas.hotel.dto.RoomTypeResponse;
import com.atlas.hotel.dto.UpdateHotelRequest;
import com.atlas.hotel.entity.Hotel;
import com.atlas.hotel.entity.HotelStatus;
import com.atlas.hotel.entity.Money;
import com.atlas.hotel.entity.RoomType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Test Data Builders for the hotel catalog (coding-standards §Unit Tests).
 */
public final class HotelTestData {

  public static final UUID HOTEL_ID = UUID.fromString("d1111111-1111-1111-1111-111111111111");
  public static final UUID STANDARD_ROOM_ID = UUID.fromString(
      "e1111111-1111-1111-1111-111111111111");

  public static final String NAME = "Atlas Grand Lima";
  public static final String CITY = "Lima";
  public static final String COUNTRY = "PE";
  public static final int RATING = 5;
  public static final String STANDARD = "Standard";
  public static final int STANDARD_TOTAL_ROOMS = 100;

  private HotelTestData() {
  }

  public static MoneyRequest aMoneyRequest() {
    return new MoneyRequest(new BigDecimal("120.00"), "USD");
  }

  public static RoomTypeInput aRoomType(String name, int totalRooms) {
    return new RoomTypeInput(name, totalRooms, 2, aMoneyRequest(), List.of());
  }

  public static CreateHotelRequest aCreateHotelRequest() {
    return new CreateHotelRequest(
        NAME,
        CITY,
        COUNTRY,
        RATING,
        List.of(aRoomType(STANDARD, STANDARD_TOTAL_ROOMS)),
        List.of("WiFi"),
        List.of(new HotelImageDto("https://cdn.atlas.local/h1.jpg", "Lobby")));
  }

  public static UpdateHotelRequest anUpdateHotelRequest(List<RoomTypeInput> roomTypes) {
    return new UpdateHotelRequest(NAME, CITY, COUNTRY, RATING, roomTypes, List.of("WiFi"),
        List.of());
  }

  public static UpdateHotelRequest anUpdateHotelRequest(int standardTotalRooms) {
    return anUpdateHotelRequest(List.of(aRoomType(STANDARD, standardTotalRooms)));
  }

  /**
   * Hotel with one existing "Standard" room type (id {@link #STANDARD_ROOM_ID}, 100 rooms).
   */
  public static Hotel aHotel() {
    Hotel hotel = new Hotel(
        HOTEL_ID,
        NAME,
        CITY,
        COUNTRY,
        RATING,
        HotelStatus.ACTIVE
    );
    hotel.addRoomType(new RoomType(
        STANDARD_ROOM_ID,
        STANDARD,
        STANDARD_TOTAL_ROOMS,
        2,
        new Money(new BigDecimal("120.00"), "USD")));
    return hotel;
  }

  public static HotelResponse aHotelResponse() {
    return new HotelResponse(
        HOTEL_ID,
        NAME,
        CITY,
        COUNTRY,
        RATING,
        HotelStatus.ACTIVE,
        List.of(new RoomTypeResponse(STANDARD_ROOM_ID, STANDARD, STANDARD_TOTAL_ROOMS, 2,
            new MoneyResponse(new BigDecimal("120.00"), "USD"), List.of())),
        List.of("WiFi"),
        List.of(new HotelImageDto("https://cdn.atlas.local/h1.jpg", "Lobby")));
  }
}
