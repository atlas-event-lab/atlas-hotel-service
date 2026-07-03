package com.atlas.hotel.controller;

import com.atlas.hotel.config.SecurityConfig;
import com.atlas.hotel.dto.MoneyResponse;
import com.atlas.hotel.dto.RoomTypePriceResponse;
import com.atlas.hotel.entity.HotelStatus;
import com.atlas.hotel.exception.HotelNotFoundException;
import com.atlas.hotel.exception.RoomTypeNotFoundException;
import com.atlas.hotel.service.HotelService;
import com.atlas.hotel.support.HotelTestData;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomTypePriceController.class)
@Import(SecurityConfig.class)
class RoomTypePriceControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private HotelService hotelService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @MockitoBean
  private Tracer tracer;

  private static final String PRICE_URL = "/api/v1/hotels/{hotelId}/room-types/{roomTypeId}/price";

  @Test
  void getRoomTypePrice_authenticated_returns200() throws Exception {
    RoomTypePriceResponse price = new RoomTypePriceResponse(
        HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID,
        new MoneyResponse(new BigDecimal("120.00"), "USD"),
        HotelStatus.ACTIVE);
    when(hotelService.getRoomTypePrice(HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID))
        .thenReturn(price);

    mvc.perform(get(PRICE_URL, HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.hotelId").value(HotelTestData.HOTEL_ID.toString()))
        .andExpect(jsonPath("$.roomTypeId").value(HotelTestData.STANDARD_ROOM_ID.toString()))
        .andExpect(jsonPath("$.pricePerNight.amount").value(120.00))
        .andExpect(jsonPath("$.pricePerNight.currency").value("USD"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void getRoomTypePrice_withdrawnHotel_returnsWithdrawnStatus() throws Exception {
    RoomTypePriceResponse price = new RoomTypePriceResponse(
        HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID,
        new MoneyResponse(new BigDecimal("120.00"), "USD"),
        HotelStatus.WITHDRAWN);
    when(hotelService.getRoomTypePrice(HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID))
        .thenReturn(price);

    mvc.perform(get(PRICE_URL, HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("WITHDRAWN"));
  }

  @Test
  void getRoomTypePrice_hotelNotFound_returns404() throws Exception {
    when(hotelService.getRoomTypePrice(HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID))
        .thenThrow(new HotelNotFoundException(HotelTestData.HOTEL_ID));

    mvc.perform(get(PRICE_URL, HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID).with(jwt()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }

  @Test
  void getRoomTypePrice_roomTypeNotFound_returns404() throws Exception {
    UUID unknownRoomTypeId = UUID.fromString("f9999999-9999-9999-9999-999999999999");
    when(hotelService.getRoomTypePrice(HotelTestData.HOTEL_ID, unknownRoomTypeId))
        .thenThrow(new RoomTypeNotFoundException(HotelTestData.HOTEL_ID, unknownRoomTypeId));

    mvc.perform(get(PRICE_URL, HotelTestData.HOTEL_ID, unknownRoomTypeId).with(jwt()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }

  @Test
  void getRoomTypePrice_nonAdminUser_returns200() throws Exception {
    RoomTypePriceResponse price = new RoomTypePriceResponse(
        HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID,
        new MoneyResponse(new BigDecimal("120.00"), "USD"),
        HotelStatus.ACTIVE);
    when(hotelService.getRoomTypePrice(HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID))
        .thenReturn(price);

    mvc.perform(get(PRICE_URL, HotelTestData.HOTEL_ID, HotelTestData.STANDARD_ROOM_ID).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hotelId").value(HotelTestData.HOTEL_ID.toString()));
  }
}
