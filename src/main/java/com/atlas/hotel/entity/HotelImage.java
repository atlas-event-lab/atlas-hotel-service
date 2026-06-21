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

/** An image of a {@link Hotel}. The event payload carries only the URL (hotel-events.yaml). */
@Entity
@Table(name = "hotel_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HotelImage {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Setter
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @Column(name = "caption")
    private String caption;

    public HotelImage(UUID id, String url, String caption) {
        this.id = id;
        this.url = url;
        this.caption = caption;
    }
}
