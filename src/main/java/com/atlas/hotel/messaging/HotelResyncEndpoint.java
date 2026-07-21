package com.atlas.hotel.messaging;

import com.atlas.hotel.service.HotelCatalogReconciler;
import com.atlas.hotel.service.ResyncResult;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

/**
 * Operational endpoint to resync the hotel catalog for a read-model rebuild (ADR-0026, Experiment
 * 07): {@code POST /actuator/resync} re-emits the current state of every hotel (and its room types)
 * through the outbox, which Search re-projects into hotel projections + the per-night calendar.
 * <p>
 * Same operational-control pattern as {@code dlqreplay} (ADR-0022): internal management port only
 * (9090, not published via ingress), network- + RBAC-gated via the k8s API proxy. Deliberate, no
 * redeploy; run in a maintenance window as part of an orchestrated rebuild (catalog before
 * availability).
 */
@Component
@Endpoint(id = "resync")
@RequiredArgsConstructor
public class HotelResyncEndpoint {

  private final HotelCatalogReconciler reconciler;

  @WriteOperation
  public ResyncResult resync() {
    return reconciler.resyncAll();
  }
}
