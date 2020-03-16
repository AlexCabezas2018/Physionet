
/*
INSERT INTO message(id, content, seen, timestamp, user_from_id, user_to_id) VALUES(1, 'test1', false, '2020-03-10', 1, 2);
INSERT INTO message(id, content, seen, timestamp, user_from_id, user_to_id) VALUES(2, 'test2', false, '2020-03-10', 2, 1);

*/

INSERT INTO user values(1, 1, -1,'Arturo', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'PATIENT', 'Martínez', 'patient');
INSERT INTO user values(2, 1, 10,'Arturo', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'DOCTOR', 'Martínez', 'doctor');
INSERT INTO user values(3, 1, -1,'Arturo', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'ADMIN', 'Martínez', 'admin');

INSERT INTO absence(id, date_from, date_to, details, reason, user_id) VALUES(1, '2020-03-07', '2020-03-10', 'detail_test_1', '2', 2);
