package com.atlas.hotel.controller;

import com.atlas.hotel.service.HotelCatalogReconciler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hotels/reconciliation")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class HotelReconciliationController {

  private final HotelCatalogReconciler reconciler;

  @PostMapping
  public ResponseEntity<String> reconcile() {
    reconciler.reconcile();
    return ResponseEntity.ok("SUCCESS");
  }
}
