INSERT INTO "jumptype" ("id", "jumptype_name", "created_at", "updated_at") VALUES
(1, 'Belly', NOW(), NOW()),
(2, 'Angle', NOW(), NOW()),
(3, 'Freefly', NOW(), NOW()),
(4, 'Formation', NOW(), NOW());
ALTER TABLE "jumptype" ALTER COLUMN "id" RESTART WITH 5;