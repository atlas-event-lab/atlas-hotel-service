-- Hotel catalog: Hotel aggregate root + RoomType (capacity + price), Amenity, HotelImage.
-- hotel_id is a LOCAL lookup reference (ARCH-004), never a cross-service FK.

CREATE TABLE hotels
(
    id         UUID                     NOT NULL,
    name       VARCHAR(255)             NOT NULL,
    city       VARCHAR(255)             NOT NULL,
    country    VARCHAR(2)               NOT NULL,
    rating     INTEGER                  NOT NULL,
    status     VARCHAR(20)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_hotels PRIMARY KEY (id),
    -- Business uniqueness key (services/hotel/service.md): a duplicate create returns 409.
    CONSTRAINT uq_hotels_name_city UNIQUE (name, city)
);

CREATE INDEX idx_hotels_status ON hotels (status);

CREATE TABLE room_types
(
    id                     UUID                     NOT NULL,
    hotel_id               UUID                     NOT NULL,
    name                   VARCHAR(255)             NOT NULL,
    total_rooms            INTEGER                  NOT NULL,
    max_occupancy          INTEGER                  NOT NULL,
    price_per_night_amount NUMERIC(19, 2)           NOT NULL,
    currency               VARCHAR(3)               NOT NULL,
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_room_types PRIMARY KEY (id),
    CONSTRAINT fk_room_types_hotel FOREIGN KEY (hotel_id) REFERENCES hotels (id),
    -- name is the stable intra-hotel key used to preserve identity across updates.
    CONSTRAINT uq_room_types_hotel_name UNIQUE (hotel_id, name)
);

CREATE INDEX idx_room_types_hotel_id ON room_types (hotel_id);

CREATE TABLE amenities
(
    id       UUID         NOT NULL,
    hotel_id UUID         NOT NULL,
    name     VARCHAR(255) NOT NULL,
    CONSTRAINT pk_amenities PRIMARY KEY (id),
    CONSTRAINT fk_amenities_hotel FOREIGN KEY (hotel_id) REFERENCES hotels (id)
);

CREATE INDEX idx_amenities_hotel_id ON amenities (hotel_id);

CREATE TABLE hotel_images
(
    id       UUID          NOT NULL,
    hotel_id UUID          NOT NULL,
    url      VARCHAR(2048) NOT NULL,
    caption  VARCHAR(512),
    CONSTRAINT pk_hotel_images PRIMARY KEY (id),
    CONSTRAINT fk_hotel_images_hotel FOREIGN KEY (hotel_id) REFERENCES hotels (id)
);

CREATE INDEX idx_hotel_images_hotel_id ON hotel_images (hotel_id);
