USE skydivingrest1;

DELETE FROM jump where id > 0 AND id < 50;
DELETE FROM aircraft where id > 0 AND id < 50;
DELETE FROM jumptype where id > 0 AND id < 50;
DELETE FROM dropzone where id > 0 AND id < 50;
DELETE FROM user where id > 0 AND id < 50;

SELECT * FROM user;
SELECT * FROM aircraft;
SELECT * FROM dropzone;
SELECT * FROM jumptype;
