INSERT INTO "aircraft" ("id", "aircraft_name", "created_at", "updated_at") VALUES
(1, 'Cessna 208 - Caravan', NOW(), NOW()),
(2, 'Cessna 206 - Stationair', NOW(), NOW()),
(3, 'Cessna 182 - Skylane', NOW(), NOW()),
(4, 'Helicopter Bell 412', NOW(), NOW()),
(5, 'Falcon9 - SpaceX Rocket', NOW(), NOW());
ALTER TABLE "aircraft" ALTER COLUMN "id" RESTART WITH 6;