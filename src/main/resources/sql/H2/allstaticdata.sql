INSERT INTO "aircraft" ("id", "aircraft_name", "created_at", "updated_at") VALUES
(1, 'Cessna 208 - Caravan', NOW(), NOW()),
(2, 'Cessna 206 - Stationair', NOW(), NOW()),
(3, 'Cessna 182 - Skylane', NOW(), NOW()),
(4, 'Helicopter Bell 412', NOW(), NOW()),
(5, 'Falcon9 - SpaceX Rocket', NOW(), NOW());

INSERT INTO "dropzone" ("id", "dropzone_name", "created_at", "updated_at") VALUES
(1, 'Hellenic Skydivers - Thiva Perneri Airfield', NOW(), NOW()),
(2, 'Skydive Athens - Kopaida Airport Kastro', NOW(), NOW()),
(3, 'Skydive Attica - Megara General Aviation Airport ', NOW(), NOW()),
(4, 'Skydive Greece - Megara General Aviation Airport', NOW(), NOW()),
(5, 'Skydive Thessaloniki - Chortero Airport', NOW(), NOW());

INSERT INTO "jumptype" ("id", "jumptype_name", "created_at", "updated_at") VALUES
(1, 'Belly', NOW(), NOW()),
(2, 'Angle', NOW(), NOW()),
(3, 'Freefly', NOW(), NOW()),
(4, 'Formation', NOW(), NOW());