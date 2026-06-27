package com.atlas.hotel.entity;

import com.atlas.hotel.dto.RoomImageDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoomImage {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private UUID id;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private RoomType room;

  @Column(name = "url", nullable = false, length = 2048)
  private String url;

  @Column(name = "caption")
  private String caption;

  public RoomImage(UUID id, String url, String caption) {
    this.id = id;
    this.caption = caption;
    this.url = url;

  }

  public RoomImageDto toRoomImageDto() {
    return new RoomImageDto(url, caption);
  }
}
