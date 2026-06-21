package com.atlas.hotel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/** A named amenity offered by a {@link Hotel} (e.g. WiFi, Pool). */
@Entity
@Table(name = "amenities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Amenity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Setter
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "name", nullable = false)
    private String name;

    public Amenity(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
