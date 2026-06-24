BEGIN;

-- =============================================================================
-- HOTELS
-- =============================================================================

INSERT INTO hotels (id, name, city, country, rating, status, created_at, updated_at)
VALUES
    ('7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','Azure Grand Lima','Lima','PE',5,'ACTIVE',now(),now()),
    ('d5d85a2e-fb80-4327-8e5f-03c74ab1bb0d','Pacific Crown Cusco','Cusco','PE',4,'ACTIVE',now(),now()),
    ('bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','Ocean Breeze Cancún','Cancún','MX',5,'ACTIVE',now(),now()),
    ('8f8d7db9-31fd-45d6-a9d2-8b2f6ef74db6','Royal Andes Santiago','Santiago','CL',4,'ACTIVE',now(),now()),
    ('9d843d44-7d6c-4f28-b5d5-f48c5ddfc4b4','Skyline Business Bogotá','Bogotá','CO',4,'ACTIVE',now(),now()),
    ('73ecb2fa-9036-4e96-8b78-52ab739cf72a','Golden Horizon Buenos Aires','Buenos Aires','AR',5,'ACTIVE',now(),now()),
    ('bd8c6441-fd4d-4c59-9304-352af3fd78da','Vista Real Montevideo','Montevideo','UY',4,'ACTIVE',now(),now()),
    ('7cb86f57-4c80-4b34-b1d3-1d4d4efecfe7','Aurora Suites Medellín','Medellín','CO',3,'ACTIVE',now(),now());

-- =============================================================================
-- ROOM TYPES
-- =============================================================================

INSERT INTO room_types
(id, hotel_id, name, total_rooms, max_occupancy, price_per_night_amount, currency, created_at, updated_at)
VALUES

    ('09b31863-d8a6-4ef4-a095-bd5fbbce3b57','7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','Standard King',70,2,165.00,'USD',now(),now()),
    ('30c7c9d4-85d5-44a2-a826-c80dd7b0d70d','7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','Deluxe King',45,3,245.00,'USD',now(),now()),
    ('70d3c3d7-83e2-4490-9b62-b4f7b1de44cb','7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','Junior Suite',18,4,395.00,'USD',now(),now()),
    ('95b39f5b-9d17-4952-99e0-fd84432f6134','7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','Presidential Suite',4,6,850.00,'USD',now(),now()),

    ('ab86b9d2-68a2-4e3c-a45d-4c3b8d16d889','d5d85a2e-fb80-4327-8e5f-03c74ab1bb0d','Standard Queen',55,2,115.00,'USD',now(),now()),
    ('d0a2eb8d-c57b-4975-8b84-d4b05a16d7df','d5d85a2e-fb80-4327-8e5f-03c74ab1bb0d','Deluxe Room',25,3,180.00,'USD',now(),now()),
    ('4e60d42b-3f34-40f5-8d48-4b5d3ec9cf53','d5d85a2e-fb80-4327-8e5f-03c74ab1bb0d','Suite',10,4,290.00,'USD',now(),now()),

    ('c80bfa4b-816d-44aa-b197-905a67fa10fd','bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','Garden View',60,2,210.00,'USD',now(),now()),
    ('430f3f96-3d89-4f9a-aac9-c67b5a2cf4f4','bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','Ocean View',45,3,320.00,'USD',now(),now()),
    ('e4db5a1d-68d5-4fd3-a730-2eaa4e30773a','bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','Master Suite',12,4,560.00,'USD',now(),now()),

    ('8d0ddbc5-7cf5-42f7-8451-16a76dbb9d84','8f8d7db9-31fd-45d6-a9d2-8b2f6ef74db6','Standard King',70,2,145.00,'USD',now(),now()),
    ('51495d90-9833-4723-b0d2-38b2bc9c2214','8f8d7db9-31fd-45d6-a9d2-8b2f6ef74db6','Executive Room',20,2,245.00,'USD',now(),now()),
    ('9f65db55-d887-46cb-bb97-5af8ef5cfc8f','8f8d7db9-31fd-45d6-a9d2-8b2f6ef74db6','Junior Suite',8,4,365.00,'USD',now(),now()),

    ('4b3f08d0-8f31-4218-83d5-9e7dc4f53a53','9d843d44-7d6c-4f28-b5d5-f48c5ddfc4b4','Standard',90,2,135.00,'USD',now(),now()),
    ('f293f607-33a4-42a4-8ec8-4f5c9ddfc9e8','9d843d44-7d6c-4f28-b5d5-f48c5ddfc4b4','Business',40,2,195.00,'USD',now(),now()),
    ('e8bb0d0f-bd14-4f27-8782-d1d67778ec76','9d843d44-7d6c-4f28-b5d5-f48c5ddfc4b4','Executive Suite',10,4,340.00,'USD',now(),now()),

    ('d8a8f30d-6d8d-4c9e-a6d7-d9894d10d0ef','73ecb2fa-9036-4e96-8b78-52ab739cf72a','Deluxe King',85,2,240.00,'USD',now(),now()),
    ('af93d94d-f62f-4e7d-b489-d6f14e1d57b3','73ecb2fa-9036-4e96-8b78-52ab739cf72a','Junior Suite',24,4,410.00,'USD',now(),now()),
    ('55db5fd7-5985-44cb-88bc-31a2e69eafdd','73ecb2fa-9036-4e96-8b78-52ab739cf72a','Presidential Suite',3,6,990.00,'USD',now(),now()),

    ('cb70c6fa-b7d0-48b5-bd28-4d2a7b4f3fc4','bd8c6441-fd4d-4c59-9304-352af3fd78da','Standard',50,2,125.00,'USD',now(),now()),
    ('efb31c55-daf2-4e39-a3e7-19631f8ebceb','bd8c6441-fd4d-4c59-9304-352af3fd78da','Suite',12,4,260.00,'USD',now(),now()),

    ('3d80d1db-c685-4ef5-9506-97fc6fd3223d','7cb86f57-4c80-4b34-b1d3-1d4d4efecfe7','Standard Queen',65,2,95.00,'USD',now(),now()),
    ('4cf2c10c-0bb5-43df-88d6-cfe3cbdd9f27','7cb86f57-4c80-4b34-b1d3-1d4d4efecfe7','Family Room',18,4,175.00,'USD',now(),now());

-- =============================================================================
-- AMENITIES
-- =============================================================================

INSERT INTO amenities (id, hotel_id, name)
VALUES
    (gen_random_uuid(),'7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','WiFi'),
    (gen_random_uuid(),'7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','Outdoor Pool'),
    (gen_random_uuid(),'7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','Spa'),
    (gen_random_uuid(),'7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','Fitness Center'),
    (gen_random_uuid(),'7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','Restaurant'),
    (gen_random_uuid(),'7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','Airport Shuttle'),

    (gen_random_uuid(),'d5d85a2e-fb80-4327-8e5f-03c74ab1bb0d','WiFi'),
    (gen_random_uuid(),'d5d85a2e-fb80-4327-8e5f-03c74ab1bb0d','Restaurant'),
    (gen_random_uuid(),'d5d85a2e-fb80-4327-8e5f-03c74ab1bb0d','Parking'),
    (gen_random_uuid(),'d5d85a2e-fb80-4327-8e5f-03c74ab1bb0d','Room Service'),

    (gen_random_uuid(),'bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','Private Beach'),
    (gen_random_uuid(),'bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','Infinity Pool'),
    (gen_random_uuid(),'bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','Spa'),
    (gen_random_uuid(),'bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','Bar'),
    (gen_random_uuid(),'bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','Kids Club'),

    (gen_random_uuid(),'8f8d7db9-31fd-45d6-a9d2-8b2f6ef74db6','WiFi'),
    (gen_random_uuid(),'8f8d7db9-31fd-45d6-a9d2-8b2f6ef74db6','Business Center'),
    (gen_random_uuid(),'8f8d7db9-31fd-45d6-a9d2-8b2f6ef74db6','Meeting Rooms'),

    (gen_random_uuid(),'9d843d44-7d6c-4f28-b5d5-f48c5ddfc4b4','WiFi'),
    (gen_random_uuid(),'9d843d44-7d6c-4f28-b5d5-f48c5ddfc4b4','Gym'),
    (gen_random_uuid(),'9d843d44-7d6c-4f28-b5d5-f48c5ddfc4b4','Coffee Shop'),

    (gen_random_uuid(),'73ecb2fa-9036-4e96-8b78-52ab739cf72a','Spa'),
    (gen_random_uuid(),'73ecb2fa-9036-4e96-8b78-52ab739cf72a','Concierge'),
    (gen_random_uuid(),'73ecb2fa-9036-4e96-8b78-52ab739cf72a','Fine Dining'),

    (gen_random_uuid(),'bd8c6441-fd4d-4c59-9304-352af3fd78da','WiFi'),
    (gen_random_uuid(),'bd8c6441-fd4d-4c59-9304-352af3fd78da','Breakfast Included'),

    (gen_random_uuid(),'7cb86f57-4c80-4b34-b1d3-1d4d4efecfe7','WiFi'),
    (gen_random_uuid(),'7cb86f57-4c80-4b34-b1d3-1d4d4efecfe7','Laundry'),
    (gen_random_uuid(),'7cb86f57-4c80-4b34-b1d3-1d4d4efecfe7','Parking');

-- =============================================================================
-- HOTEL IMAGES
-- =============================================================================

INSERT INTO hotel_images (id, hotel_id, url, caption)
VALUES
    (gen_random_uuid(),'7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','https://cdn.atlas.local/hotels/azure-grand-lima/exterior.jpg','Exterior'),
    (gen_random_uuid(),'7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','https://cdn.atlas.local/hotels/azure-grand-lima/lobby.jpg','Lobby'),
    (gen_random_uuid(),'7d7d5a9d-5c24-4d84-a63b-fbf1d5a4c7c1','https://cdn.atlas.local/hotels/azure-grand-lima/pool.jpg','Pool'),

    (gen_random_uuid(),'d5d85a2e-fb80-4327-8e5f-03c74ab1bb0d','https://cdn.atlas.local/hotels/pacific-crown-cusco/exterior.jpg','Facade'),
    (gen_random_uuid(),'d5d85a2e-fb80-4327-8e5f-03c74ab1bb0d','https://cdn.atlas.local/hotels/pacific-crown-cusco/room.jpg','Deluxe Room'),

    (gen_random_uuid(),'bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','https://cdn.atlas.local/hotels/ocean-breeze-cancun/beach.jpg','Private Beach'),
    (gen_random_uuid(),'bd85cfd7-6af0-4c5f-a58d-c27831d6d4b5','https://cdn.atlas.local/hotels/ocean-breeze-cancun/pool.jpg','Infinity Pool'),

    (gen_random_uuid(),'8f8d7db9-31fd-45d6-a9d2-8b2f6ef74db6','https://cdn.atlas.local/hotels/royal-andes-santiago/lobby.jpg','Lobby'),

    (gen_random_uuid(),'9d843d44-7d6c-4f28-b5d5-f48c5ddfc4b4','https://cdn.atlas.local/hotels/skyline-business-bogota/business-center.jpg','Business Center'),

    (gen_random_uuid(),'73ecb2fa-9036-4e96-8b78-52ab739cf72a','https://cdn.atlas.local/hotels/golden-horizon-buenos-aires/suite.jpg','Presidential Suite'),

    (gen_random_uuid(),'bd8c6441-fd4d-4c59-9304-352af3fd78da','https://cdn.atlas.local/hotels/vista-real-montevideo/exterior.jpg','Exterior'),

    (gen_random_uuid(),'7cb86f57-4c80-4b34-b1d3-1d4d4efecfe7','https://cdn.atlas.local/hotels/aurora-suites-medellin/room.jpg','Standard Room');

COMMIT;