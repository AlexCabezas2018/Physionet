INSERT INTO user values(1, 1, -1,'Arturo',
'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'PATIENT', 'Martínez', 'patient');
INSERT INTO user values(2, 1, 10,'Arturo', 
'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'DOCTOR', 'García', 'doctor');
INSERT INTO user values(3, 1, 10,'Fernando',
'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'DOCTOR', 'Martínez', 'doctor2');
INSERT INTO user values(4, 1, -1,'Arturo',
'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'ADMIN', 'Martínez', 'admin');
INSERT INTO user values(5, 1, -1,'Paco',
'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'PATIENT', 'Morcuende', 'Jose');
INSERT INTO user values(6, 1, -1,'Miguel',
'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'PATIENT', 'Muñoz', 'Miguelito');
INSERT INTO user values(7, 1, -1,'Jorge',
'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'PATIENT', 'García', 'Jorgito');


INSERT INTO absence(id, date_from, date_to, details, reason, status, user_id) VALUES(1, '2020-04-05', '2020-04-08', 'detail_test_1', '2', '0', 3);

/* Mensaje de un médico a un paciente */
INSERT INTO message values(1, '2020-03-16', '2020-02-27 12:14:07', 'Hola que tal soy el chicos de las poesias', 1, 2);
INSERT INTO message values(2, null, '2020-02-27 12:25:27', 'tu fiel admirador', 2, 1);
INSERT INTO message values(3, null, '2020-02-27 12:34:57', 'tu', 2, 1);
INSERT INTO message values(4, '2020-03-16', '2020-02-27 12:14:24', 'Go to the diskotek', 1, 2);
INSERT INTO message values(5, '2020-03-16', '2020-02-27 12:14:43', 'Ah', 1, 2);
INSERT INTO message values(6, '2020-03-16', '2020-02-27 12:15:07', 'Que no se puede', 1, 2);

INSERT INTO message values(7, '2020-03-16', '2020-02-27 08:15:07', 'Hola k tal', 5, 2);
INSERT INTO message values(8, '2020-03-16', '2020-02-27 10:24:35', 'Hola k tal', 2, 5);

INSERT INTO appointment values(1,
    NOW(),'Dolor de espalda',true,'Sala 1','MasajeS de espalda','',2,1);
INSERT INTO appointment values(2,
    NOW(),'Dolor de espalda 2',true,'Sala 1','Masaje de espalda','Tumbarse en el suelo',2,1);
INSERT INTO appointment values(3,
    '2020-05-01 16:30:35','Dolor lumbar',false,'Sala 2','Masaje zona lumbar','',2,1);
INSERT INTO appointment values(4,
    '2020-05-23 19:24:35','Dolor lumbar 2',false,'Sala 2','Masaje zona lummbar leve','',2,1);
INSERT INTO appointment values(5,
    '2020-05-09 23:30:35','Dolor intenso en la zona del gemelo',false,'Sala 8','Dolor pierna','',2,1);