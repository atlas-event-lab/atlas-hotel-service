package com.atlas.hotel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Room type in create/update requests (hotel.yaml RoomTypeInput). Carries no id — identity is
 *  derived from {@code name} on update (resolved Open Question). */
public record RoomTypeInput(

        @NotBlank
        String name,

        @NotNull
        @Min(1)
        Integer totalRooms,

        @NotNull
        @Min(1)
        Integer maxOccupancy,

        @NotNull
        @Valid
        MoneyRequest pricePerNight
) {}
