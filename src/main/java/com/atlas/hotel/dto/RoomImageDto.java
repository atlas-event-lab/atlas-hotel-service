package com.atlas.hotel.dto;

import jakarta.validation.constraints.NotBlank;

public record RoomImageDto(
  @NotBlank
  String url,

  String caption
) {}
