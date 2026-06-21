package com.atlas.hotel.service;

import com.atlas.hotel.entity.Hotel;
import com.atlas.hotel.event.HotelEventPayloadFactory;
import com.atlas.hotel.messaging.OutboxEventWriter;
import com.atlas.hotel.repository.HotelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelCatalogReconciler {

  private final HotelRepository hotelRepository;
  private final OutboxEventWriter outboxEventWriter;
  private final HotelEventPayloadFactory payloadFactory;

  @Transactional
  public void reconcile() {

    List<Hotel> hotels = hotelRepository.findHotelsWithoutCreatedEvent();
    log.info("{} hotels found without Created Event", hotels.size());

    hotels.forEach(hotel ->
        outboxEventWriter.write(
            hotel.getId(),
            "FlightCreated",
            payloadFactory.toCatalogPayload(hotel)
        ));
  }
}

