package com.atlas.hotel.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * A bookable room category within a {@link Hotel}. Capacity ({@code totalRooms}) and price live
 * here (services/hotel/service.md). {@code name} is the stable intra-hotel key used to preserve
 * identity across updates; live availability is owned by Inventory (ARCH-002).
 */
@Entity
@Table(name = "room_types")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoomType {

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

    @Column(name = "total_rooms", nullable = false)
    private int totalRooms;

    @Column(name = "max_occupancy", nullable = false)
    private int maxOccupancy;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",   column = @Column(name = "price_per_night_amount", nullable = false, precision = 19, scale = 2)),
            @AttributeOverride(name = "currency", column = @Column(name = "currency",                nullable = false, length = 3))
    })
    private Money pricePerNight;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RoomImage> images = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public RoomType(UUID id, String name, int totalRooms, int maxOccupancy, Money pricePerNight) {
        this.id = id;
        this.name = name;
        this.totalRooms = totalRooms;
        this.maxOccupancy = maxOccupancy;
        this.pricePerNight = pricePerNight;
    }

    /** In-place merge on update; preserves {@code id} so Inventory availability stays linked. */
    public void update(int totalRooms, int maxOccupancy, Money pricePerNight) {
        this.totalRooms = totalRooms;
        this.maxOccupancy = maxOccupancy;
        this.pricePerNight = pricePerNight;
    }

    public void replaceImages(List<RoomImage> newImages) {
        images.clear();
        for (RoomImage image : newImages) {
            images.add(image);
            image.setRoom(this);
        }
    }
}
