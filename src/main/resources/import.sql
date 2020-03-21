
/*
INSERT INTO message(id, content, seen, timestamp, user_from_id, user_to_id) VALUES(1, 'test1', false, '2020-03-10', 1, 2);
INSERT INTO message(id, content, seen, timestamp, user_from_id, user_to_id) VALUES(2, 'test2', false, '2020-03-10', 2, 1);

*/

INSERT INTO user values(1, 1, -1,'Arturo', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'PATIENT', 'Martínez', 'patient');
INSERT INTO user values(2, 1, 10,'Arturo', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'DOCTOR', 'Martínez', 'doctor');
INSERT INTO user values(3, 1, -1,'Arturo', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'ADMIN', 'Martínez', 'admin');
INSERT INTO user values(4, 1, -1,'Miguel', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'PATIENT', 'Martínez', 'Miguelito');

INSERT INTO absence(id, date_from, date_to, details, reason, user_id) VALUES(1, '2020-03-07', '2020-03-11', 'detail_test_1', '2', 2);

/* Mensaje de un médico a un paciente */
INSERT INTO message values(1, '2020-03-16', '2020-02-27 12:14:07', 'Hola que tal soy el chicos de las poesias', 1, 2);
INSERT INTO message values(2, null, '2020-02-27 12:25:27', 'tu fiel admirador', 2, 1);
INSERT INTO message values(3, null, '2020-02-27 12:34:57', 'tu', 2, 1);
INSERT INTO message values(4, '2020-03-16', '2020-02-27 12:14:24', 'Go to the diskotek', 1, 2);
INSERT INTO message values(5, '2020-03-16', '2020-02-27 12:14:43', 'Ah', 1, 2);
INSERT INTO message values(6, '2020-03-16', '2020-02-27 12:15:07', 'Que no se puede', 1, 2);

INSERT INTO message values(7, '2020-03-16', '2020-02-27 08:15:07', 'Hola k tal', 4, 2);
INSERT INTO message values(8, '2020-03-16', '2020-02-27 10:24:35', 'Hola k tal', 2, 4);