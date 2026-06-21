INSERT INTO hotels (id, name, city, country, rating, status, created_at, updated_at)
VALUES ('d1111111-1111-1111-1111-111111111111', 'Atlas Grand Lima', 'Lima', 'PE', 5, 'ACTIVE',
        now(), now()),
       ('d2222222-2222-2222-2222-222222222222', 'Iberia Plaza Madrid', 'Madrid', 'ES', 4, 'ACTIVE',
        now(), now());

INSERT INTO room_types (id, hotel_id, name, total_rooms, max_occupancy, price_per_night_amount,
                        currency, created_at, updated_at)
VALUES ('e1111111-1111-1111-1111-111111111111', 'd1111111-1111-1111-1111-111111111111', 'Standard',
        100, 2, 120.00, 'USD', now(), now()),
       ('e1222222-2222-2222-2222-222222222222', 'd1111111-1111-1111-1111-111111111111', 'Suite', 20,
        4, 320.00, 'USD', now(), now()),
       ('e2111111-1111-1111-1111-111111111111', 'd2222222-2222-2222-2222-222222222222', 'Standard',
        80, 2, 100.00, 'EUR', now(), now()),
       ('e2222222-2222-2222-2222-222222222222', 'd2222222-2222-2222-2222-222222222222', 'Deluxe',
        30, 3, 180.00, 'EUR', now(), now());

INSERT INTO amenities (id, hotel_id, name)
VALUES ('f1111111-1111-1111-1111-111111111111', 'd1111111-1111-1111-1111-111111111111', 'WiFi'),
       ('f1222222-2222-2222-2222-222222222222', 'd1111111-1111-1111-1111-111111111111', 'Pool'),
       ('f2111111-1111-1111-1111-111111111111', 'd2222222-2222-2222-2222-222222222222', 'WiFi');

INSERT INTO hotel_images (id, hotel_id, url, caption)
VALUES ('aa111111-1111-1111-1111-111111111111', 'd1111111-1111-1111-1111-111111111111',
        'https://cdn.atlas.local/hotels/grand-lima-1.jpg', 'Lobby'),
       ('aa222222-2222-2222-2222-222222222222', 'd2222222-2222-2222-2222-222222222222',
        'https://cdn.atlas.local/hotels/plaza-madrid-1.jpg', 'Facade');
