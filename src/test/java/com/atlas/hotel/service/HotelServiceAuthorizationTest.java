package com.atlas.hotel.service;

import com.atlas.hotel.client.InventoryClient;
import com.atlas.hotel.event.HotelEventPayloadFactory;
import com.atlas.hotel.mapper.HotelMapper;
import com.atlas.hotel.messaging.OutboxEventWriter;
import com.atlas.hotel.repository.HotelRepository;
import com.atlas.hotel.support.HotelTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Verifies that RBAC {@code ADMIN} is enforced inside the business service via
 * {@code @PreAuthorize} (SEC-004). Loads only the service + method-security advisors so the
 * proxy is active; all collaborators are mocked.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HotelServiceImpl.class, HotelServiceAuthorizationTest.MethodSecurityConfig.class})
class HotelServiceAuthorizationTest {

    @Configuration
    @EnableMethodSecurity
    static class MethodSecurityConfig {}

    @MockitoBean HotelRepository hotelRepository;
    @MockitoBean InventoryClient inventoryClient;
    @MockitoBean OutboxEventWriter outboxEventWriter;
    @MockitoBean HotelEventPayloadFactory payloadFactory;
    @MockitoBean HotelMapper hotelMapper;

    @Autowired
    HotelService hotelService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminRole_canInvoke() {
        when(hotelRepository.findById(any())).thenReturn(Optional.of(HotelTestData.aHotel()));
        when(hotelMapper.toResponse(any())).thenReturn(HotelTestData.aHotelResponse());

        assertThat(hotelService.getHotel(HotelTestData.HOTEL_ID)).isNotNull();
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdminRole_isForbidden() {
        assertThatThrownBy(() -> hotelService.getHotel(HotelTestData.HOTEL_ID))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithAnonymousUser
    void anonymous_isForbidden() {
        assertThatThrownBy(() -> hotelService.getHotel(HotelTestData.HOTEL_ID))
                .isInstanceOfAny(AccessDeniedException.class, AuthenticationCredentialsNotFoundException.class);
    }
}
