package com.atlas.hotel.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Aggregate Root for the hotel catalog. Owns descriptive data, room types (capacity + price),
 * amenities and images (services/hotel/service.md). Live room availability is NOT owned here —
 * Inventory owns that per room type (ARCH-002, DB-001).
 */
@Entity
@Table(name = "hotels")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Hotel {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(name = "name", nullable = false)
    @ToString.Include
    private String name;

    @Column(name = "city", nullable = false)
    @ToString.Include
    private String city;

    @Column(name = "country", nullable = false, length = 2)
    private String country;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private HotelStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("name ASC")
    private List<RoomType> roomTypes = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Amenity> amenities = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HotelImage> images = new ArrayList<>();

    public Hotel(UUID id, String name, String city, String country, int rating, HotelStatus status) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.rating = rating;
        this.status = status;
    }

    /** Replaces descriptive fields on update (PUT semantics). Room types/amenities/images are
     *  reconciled/replaced separately. */
    public void update(String name, String city, String country, int rating) {
        this.name = name;
        this.city = city;
        this.country = country;
        this.rating = rating;
    }

    /** Soft-deactivates the hotel (no hard delete); it is no longer bookable. */
    public void withdraw() {
        this.status = HotelStatus.WITHDRAWN;
    }

    public void addRoomType(RoomType roomType) {
        roomTypes.add(roomType);
        roomType.setHotel(this);
    }

    /**
     * Reconciles room types by {@code name} (the resolved Open Question): a same-name room type
     * keeps its {@code id} and is updated in place; a new name is added with a fresh {@code id};
     * an existing name absent from {@code desired} is removed (orphanRemoval). Preserving the id
     * keeps the per-room-type Inventory availability linked.
     */
    public void reconcileRoomTypes(List<RoomType> desired) {
        Map<String, RoomType> existingByName = roomTypes.stream()
                .collect(Collectors.toMap(RoomType::getName, Function.identity()));

        List<String> desiredNames = desired.stream().map(RoomType::getName).toList();
        roomTypes.removeIf(rt -> !desiredNames.contains(rt.getName()));

        for (RoomType d : desired) {
            RoomType existing = existingByName.get(d.getName());
            if (existing != null) {
                existing.update(d.getTotalRooms(), d.getMaxOccupancy(), d.getPricePerNight());
            } else {
                addRoomType(d);
            }
        }
    }

    public void replaceAmenities(List<Amenity> newAmenities) {
        amenities.clear();
        for (Amenity amenity : newAmenities) {
            amenities.add(amenity);
            amenity.setHotel(this);
        }
    }

    public void replaceImages(List<HotelImage> newImages) {
        images.clear();
        for (HotelImage image : newImages) {
            images.add(image);
            image.setHotel(this);
        }
    }

    public List<RoomType> getRoomTypes() { return Collections.unmodifiableList(roomTypes); }
    public List<Amenity> getAmenities()  { return Collections.unmodifiableList(amenities); }
    public List<HotelImage> getImages()  { return Collections.unmodifiableList(images); }
}
