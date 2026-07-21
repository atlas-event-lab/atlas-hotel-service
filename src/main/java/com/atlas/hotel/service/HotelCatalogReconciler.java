package com.atlas.hotel.service;

import com.atlas.hotel.entity.Hotel;
import com.atlas.hotel.entity.HotelStatus;
import com.atlas.hotel.event.HotelDeletedPayload;
import com.atlas.hotel.event.HotelEventPayloadFactory;
import com.atlas.hotel.messaging.OutboxEventWriter;
import com.atlas.hotel.repository.HotelRepository;
import com.atlas.hotel.shared.messaging.EventType;
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
            EventType.HOTEL_CREATED,
            payloadFactory.toCatalogPayload(hotel)
        ));
  }

  /**
   * Full catalog resync for a read-model rebuild (ADR-0026, Experiment 07). Re-emits the current
   * state of <b>every</b> hotel (with its room types) from {@code hotel_db} through the outbox —
   * ACTIVE hotels as {@code HOTEL_CREATED} (Search upserts created/updated identically and
   * re-materializes the per-night calendar), WITHDRAWN hotels as {@code HOTEL_DELETED}. Unlike
   * {@link #reconcile()} (backfill only), this republishes unconditionally so a wiped read model can
   * be fully rebuilt.
   */
  @Transactional
  public ResyncResult resyncAll() {
    List<Hotel> active = hotelRepository.findByStatus(HotelStatus.ACTIVE);
    List<Hotel> withdrawn = hotelRepository.findByStatus(HotelStatus.WITHDRAWN);

    active.forEach(hotel ->
        outboxEventWriter.write(hotel.getId(), EventType.HOTEL_CREATED,
            payloadFactory.toCatalogPayload(hotel)));
    withdrawn.forEach(hotel ->
        outboxEventWriter.write(hotel.getId(), EventType.HOTEL_DELETED,
            new HotelDeletedPayload(hotel.getId())));

    log.warn("Catalog resync: re-emitted {} active (CREATED) + {} withdrawn (DELETED) hotel events",
        active.size(), withdrawn.size());
    return new ResyncResult(active.size(), withdrawn.size());
  }
}

