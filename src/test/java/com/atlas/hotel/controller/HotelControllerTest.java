package com.atlas.hotel.controller;

import com.atlas.hotel.config.SecurityConfig;
import com.atlas.hotel.dto.CreateHotelRequest;
import com.atlas.hotel.dto.HotelListResponse;
import com.atlas.hotel.dto.UpdateHotelRequest;
import com.atlas.hotel.exception.CapacityBelowReservedException;
import com.atlas.hotel.exception.DuplicateHotelException;
import com.atlas.hotel.exception.HotelNotFoundException;
import com.atlas.hotel.exception.InventoryUnavailableException;
import com.atlas.hotel.service.HotelService;
import com.atlas.hotel.support.HotelTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelController.class)
@Import(SecurityConfig.class)
class HotelControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private HotelService hotelService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @MockitoBean
  private Tracer tracer;

  private static final String BASE_URL = "/api/v1/hotels";

  private static RequestPostProcessor adminJwt() {
    return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  @Test
  void createHotel_valid_returns201() throws Exception {
    when(hotelService.createHotel(any(CreateHotelRequest.class))).thenReturn(
        HotelTestData.aHotelResponse());

    mvc.perform(post(BASE_URL)
            .with(adminJwt())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(HotelTestData.aCreateHotelRequest())))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.hotelId").value(HotelTestData.HOTEL_ID.toString()))
        .andExpect(jsonPath("$.status").value("ACTIVE"))
        .andExpect(jsonPath("$.roomTypes[0].name").value("Standard"));
  }

  @Test
  void createHotel_invalidBody_returns400() throws Exception {
    // Missing required fields and an empty room types list.
    String invalidBody = "{\"name\":\"\",\"roomTypes\":[]}";

    mvc.perform(post(BASE_URL)
            .with(adminJwt())
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidBody))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }

  @Test
  void createHotel_duplicate_returns409() throws Exception {
    when(hotelService.createHotel(any(CreateHotelRequest.class)))
        .thenThrow(new DuplicateHotelException(HotelTestData.NAME, HotelTestData.CITY));

    mvc.perform(post(BASE_URL)
            .with(adminJwt())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(HotelTestData.aCreateHotelRequest())))
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }

  @Test
  void createHotel_unauthenticated_returns401() throws Exception {
    mvc.perform(post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(HotelTestData.aCreateHotelRequest())))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void listHotels_returns200() throws Exception {
    when(hotelService.listHotels(any()))
        .thenReturn(new HotelListResponse(List.of(HotelTestData.aHotelResponse()), 0, 20, 1, 1));

    mvc.perform(get(BASE_URL).with(adminJwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].hotelId").value(HotelTestData.HOTEL_ID.toString()))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void getHotel_found_returns200() throws Exception {
    when(hotelService.getHotel(HotelTestData.HOTEL_ID)).thenReturn(HotelTestData.aHotelResponse());

    mvc.perform(get(BASE_URL + "/{hotelId}", HotelTestData.HOTEL_ID).with(adminJwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hotelId").value(HotelTestData.HOTEL_ID.toString()));
  }

  @Test
  void getHotel_notFound_returns404() throws Exception {
    when(hotelService.getHotel(HotelTestData.HOTEL_ID))
        .thenThrow(new HotelNotFoundException(HotelTestData.HOTEL_ID));

    mvc.perform(get(BASE_URL + "/{hotelId}", HotelTestData.HOTEL_ID).with(adminJwt()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }

  @Test
  void updateHotel_capacityBelowReserved_returns409() throws Exception {
    when(hotelService.updateHotel(eq(HotelTestData.HOTEL_ID), any(UpdateHotelRequest.class)))
        .thenThrow(
            new CapacityBelowReservedException(HotelTestData.STANDARD_ROOM_ID, "Standard", 50,
                150));

    mvc.perform(put(BASE_URL + "/{hotelId}", HotelTestData.HOTEL_ID)
            .with(adminJwt())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(HotelTestData.anUpdateHotelRequest(50))))
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }

  @Test
  void updateHotel_inventoryUnavailable_returns503() throws Exception {
    when(hotelService.updateHotel(eq(HotelTestData.HOTEL_ID), any(UpdateHotelRequest.class)))
        .thenThrow(new InventoryUnavailableException(HotelTestData.STANDARD_ROOM_ID,
            new RuntimeException("down")));

    mvc.perform(put(BASE_URL + "/{hotelId}", HotelTestData.HOTEL_ID)
            .with(adminJwt())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(HotelTestData.anUpdateHotelRequest(50))))
        .andExpect(status().isServiceUnavailable())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }

  @Test
  void withdrawHotel_returns204() throws Exception {
    doNothing().when(hotelService).withdrawHotel(HotelTestData.HOTEL_ID);

    mvc.perform(delete(BASE_URL + "/{hotelId}", HotelTestData.HOTEL_ID).with(adminJwt()))
        .andExpect(status().isNoContent());
  }

  @Test
  void withdrawHotel_notFound_returns404() throws Exception {
    doThrow(new HotelNotFoundException(HotelTestData.HOTEL_ID))
        .when(hotelService).withdrawHotel(HotelTestData.HOTEL_ID);

    mvc.perform(delete(BASE_URL + "/{hotelId}", HotelTestData.HOTEL_ID).with(adminJwt()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }
}
