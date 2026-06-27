CREATE TABLE IF NOT EXISTS room_images
(
    id       UUID          NOT NULL,
    room_id  UUID          NOT NULL,
    url      VARCHAR(2048) NOT NULL,
    caption  VARCHAR(512),
    CONSTRAINT pk_room_images PRIMARY KEY (id),
    CONSTRAINT fk_room_images_hotel FOREIGN KEY (room_id) REFERENCES room_types (id)
);

CREATE INDEX idx_room_images_room_id ON room_images (room_id);