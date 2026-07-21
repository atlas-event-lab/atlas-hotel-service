package com.atlas.hotel.service;

import com.atlas.hotel.entity.Hotel;
import com.atlas.hotel.entity.HotelStatus;
import com.atlas.hotel.event.HotelDeletedPayload;
import com.atlas.hotel.event.HotelEventPayloadFactory;
import com.atlas.hotel.messaging.OutboxEventWriter;
import com.atlas.hotel.repository.HotelRepository;
import com.atlas.hotel.shared.messaging.EventType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HotelCatalogReconcilerTest {

    private final HotelRepository hotelRepository = mock(HotelRepository.class);
    private final OutboxEventWriter outboxEventWriter = mock(OutboxEventWriter.class);
    private final HotelEventPayloadFactory payloadFactory = mock(HotelEventPayloadFactory.class);
    private final HotelCatalogReconciler reconciler =
            new HotelCatalogReconciler(hotelRepository, outboxEventWriter, payloadFactory);

    @Test
    void resyncAll_reEmitsActiveAsCreatedAndWithdrawnAsDeleted() {
        UUID activeId = UUID.randomUUID();
        UUID withdrawnId = UUID.randomUUID();
        Hotel active = mock(Hotel.class);
        Hotel withdrawn = mock(Hotel.class);
        when(active.getId()).thenReturn(activeId);
        when(withdrawn.getId()).thenReturn(withdrawnId);
        when(hotelRepository.findByStatus(HotelStatus.ACTIVE)).thenReturn(List.of(active));
        when(hotelRepository.findByStatus(HotelStatus.WITHDRAWN)).thenReturn(List.of(withdrawn));

        ResyncResult result = reconciler.resyncAll();

        verify(outboxEventWriter).write(eq(activeId), eq(EventType.HOTEL_CREATED), any());
        verify(outboxEventWriter).write(eq(withdrawnId), eq(EventType.HOTEL_DELETED),
                any(HotelDeletedPayload.class));
        assertThat(result.active()).isEqualTo(1);
        assertThat(result.withdrawn()).isEqualTo(1);
        assertThat(result.total()).isEqualTo(2);
    }
}
