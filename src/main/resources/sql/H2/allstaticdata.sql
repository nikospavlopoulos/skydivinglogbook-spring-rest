INSERT INTO "user" ("active", "created_at", "id", "updated_at", "uuid", "firstname", "lastname", "password", "username", "role") VALUES 
(TRUE, '2025-10-09 22:57:52.990003' , 1, '2025-10-09 22:57:52.990003' , '08a3d7c8-1071-4483-b5e4-7e1c55dabaaf', 'User', 'Test', '$2a$11$z1Xy1UR20rtteEqca1outOFH6RJDnNCszNHRrlh6/.iAlbHthCRX6', 'user@test.com', 'SKYDIVER');
ALTER TABLE "user" ALTER COLUMN "id" RESTART WITH 2;

INSERT INTO "aircraft" ("id", "aircraft_name", "created_at", "updated_at") VALUES
(1, 'Cessna 208 - Caravan', NOW(), NOW()),
(2, 'Cessna 206 - Stationair', NOW(), NOW()),
(3, 'Cessna 182 - Skylane', NOW(), NOW()),
(4, 'Helicopter Bell 412', NOW(), NOW()),
(5, 'Falcon9 - SpaceX Rocket', NOW(), NOW());
ALTER TABLE "aircraft" ALTER COLUMN "id" RESTART WITH 6;

INSERT INTO "dropzone" ("id", "dropzone_name", "created_at", "updated_at") VALUES
(1, 'Hellenic Skydivers - Thiva Perneri Airfield', NOW(), NOW()),
(2, 'Skydive Athens - Kopaida Airport Kastro', NOW(), NOW()),
(3, 'Skydive Attica - Megara General Aviation Airport ', NOW(), NOW()),
(4, 'Skydive Greece - Megara General Aviation Airport', NOW(), NOW()),
(5, 'Skydive Thessaloniki - Chortero Airport', NOW(), NOW());
ALTER TABLE "dropzone" ALTER COLUMN "id" RESTART WITH 6;

INSERT INTO "jumptype" ("id", "jumptype_name", "created_at", "updated_at") VALUES
(1, 'Belly', NOW(), NOW()),
(2, 'Angle', NOW(), NOW()),
(3, 'Freefly', NOW(), NOW()),
(4, 'Formation', NOW(), NOW());
ALTER TABLE "jumptype" ALTER COLUMN "id" RESTART WITH 5;

INSERT INTO "jump" ("altitude", "free_fall_duration", "aircraft_id", "created_at", "dropzone_id", "id", "jump_date", "jumptype_id", "updated_at", "user_id", "uuid", "jump_notes")
VALUES
    (10424, 59, 4, NOW(), 4, 1, '2024-01-05 10:49:06', 1, NOW(), 1, 'eb579c07-058d-4336-82df-d81bf5be984c', 'Randomized jump notes'),
    (12020, 52, 1, NOW(), 3, 2, '2024-02-01 10:49:06', 4, NOW(), 1, 'af34ca61-3e6e-4806-b3e3-15a3bd56dadb', 'Randomized jump notes'),
    (13652, 53, 3, NOW(), 2, 3, '2024-03-01 10:49:06', 3, NOW(), 1, '513dc0d3-9b52-42f7-84a7-f6b839ce366c', 'Randomized jump notes'),
    (10258, 58, 5, NOW(), 4, 4, '2024-04-01 10:49:06', 4, NOW(), 1, '7fa634dd-134f-4887-b266-8adb2a6ad133', 'Randomized jump notes'),
    (12355, 58, 4, NOW(), 2, 5, '2024-05-01 10:49:06', 3, NOW(), 1, '26e4482e-9a4e-435e-ae9d-20b789f3b226', 'Randomized jump notes'),
    (10133, 48, 4, NOW(), 5, 6, '2024-06-01 10:49:06', 4, NOW(), 1, '5d37359d-983a-4edf-aacd-8c866a8dde25', 'Randomized jump notes'),
    (11585, 50, 1, NOW(), 3, 7, '2024-07-01 10:49:06', 4, NOW(), 1, '2fa43fe5-f8b0-44b2-997c-f6957fc75a15', 'Randomized jump notes'),
    (11391, 48, 1, NOW(), 1, 8, '2024-08-01 10:49:06', 2, NOW(), 1, 'e25e3c0a-07be-463f-a918-dc1475d29f1f', 'Randomized jump notes'),
    (13308, 52, 5, NOW(), 4, 9, '2024-09-01 10:49:06', 2, NOW(), 1, 'e9a5172d-670b-4e83-b697-9bfe9a8b001b', 'Randomized jump notes'),
    (12840, 47, 2, NOW(), 4, 10, '2024-10-01 10:49:06', 4, NOW(), 1, 'a9c55209-f6e1-49d2-837a-c9b5f2fbc3da', 'Randomized jump notes'),
    (10227, 48, 1, NOW(), 3, 11, '2024-11-01 10:49:06', 4, NOW(), 1, 'a8f8d992-229d-455f-b5d1-cd72081fca40', 'Randomized jump notes'),
    (12049, 60, 3, NOW(), 2, 12, '2024-12-01 10:49:06', 3, NOW(), 1, '829b9925-899e-4b56-878b-1956b4501b99', 'Randomized jump notes'),
    (11575, 45, 1, NOW(), 2, 13, '2025-01-01 10:49:06', 3, NOW(), 1, '9fa4ee99-cdd5-4bf4-acca-abc94abb0aa8', 'Randomized jump notes'),
    (12107, 54, 1, NOW(), 3, 14, '2025-01-25 10:49:06', 1, NOW(), 1, '011fe814-133e-4568-995d-ad303862dd5d', 'Randomized jump notes'),
    (13827, 57, 4, NOW(), 4, 15, '2025-02-20 10:49:06', 4, NOW(), 1, '72818b69-fc42-47bb-851c-50bc2e43b074', 'Randomized jump notes'),
    (10929, 48, 3, NOW(), 2, 16, '2025-03-18 10:49:06', 3, NOW(), 1, '018c4edd-6820-426a-b547-eabcc66ee63f', 'Randomized jump notes'),
    (10135, 45, 4, NOW(), 5, 17, '2025-04-15 10:49:06', 2, NOW(), 1, '019ff58d-4ed0-4352-a2ef-6b1882995f34', 'Randomized jump notes'),
    (13591, 45, 4, NOW(), 3, 18, '2025-05-12 10:49:06', 1, NOW(), 1, '25458bd6-d1be-4ae3-818e-46540ab31c80', 'Randomized jump notes'),
    (10973, 45, 1, NOW(), 4, 19, '2025-06-08 10:49:06', 1, NOW(), 1, '4bcbd776-884c-407b-8c49-39593e6f150e', 'Randomized jump notes'),
    (13218, 47, 1, NOW(), 2, 20, '2025-07-05 10:49:06', 1, NOW(), 1, '50b60bd5-7163-4c5d-b819-3cfc2e6cd859', 'Randomized jump notes'),
    (10392, 48, 3, NOW(), 2, 21, '2025-07-20 10:49:06', 2, NOW(), 1, 'b9b72431-ceaf-469c-a54b-2f946ac13473', 'Randomized jump notes'),
    (13502, 54, 2, NOW(), 4, 22, '2025-07-28 10:49:06', 2, NOW(), 1, '05ac0eda-e176-486f-b153-a33ca1861f36', 'Randomized jump notes'),
    (12392, 58, 2, NOW(), 2, 23, '2025-08-02 10:49:06', 1, NOW(), 1, 'fcd7137a-0c41-429e-9817-0b1eaf1c1e1c', 'Randomized jump notes'),
    (12362, 60, 5, NOW(), 3, 24, '2025-08-06 10:49:06', 3, NOW(), 1, '61f6ad5b-3358-4d39-9310-cc1dd4ed276e', 'Randomized jump notes'),
    (12711, 56, 4, NOW(), 1, 25, '2025-08-10 10:49:06', 1, NOW(), 1, 'ae4d68cf-80cc-4d25-b2f8-6a24bc561479', 'Randomized jump notes');

ALTER TABLE "jump" ALTER COLUMN "id" RESTART WITH 26;