/*
  Aquí se debe insertar lo necesario para tener algo funcional.
*/

INSERT INTO user(id, created_at, name, password, role, surname, username) VALUES(1, null, 'Arturo', '1234', 1, 'Martinez', 'amartinez');
INSERT INTO user(id, created_at, name, password, role, surname, username) VALUES(2, null, 'Mario', '1234', 1, 'Peral', 'mperal');
INSERT INTO user(id, created_at, name, password, role, surname, username) VALUES(3, null, 'Pedro', '1234', 1, 'Izquierdo', 'pizquierdo');
INSERT INTO user(id, created_at, name, password, role, surname, username) VALUES(4, null, 'Gerardo', '1234', 1, 'Parra', 'gparra');

INSERT INTO message(id, content, seen, timestamp, user_from_id, user_to_id) VALUES(1, 'test1', false, '2020-03-10', 1, 2);
INSERT INTO message(id, content, seen, timestamp, user_from_id, user_to_id) VALUES(2, 'test2', false, '2020-03-10', 2, 1);

INSERT INTO absence(id, date_from, date_to, details, reason, user_id) VALUES(1, '2020-03-07', '2020-03-10', 'detail_test_1', '2', 1);