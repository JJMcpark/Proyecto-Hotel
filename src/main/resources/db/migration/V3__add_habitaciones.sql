-- ==========================================
-- V3__Habitaciones_Reales.sql
-- Inserción estricta de habitaciones basadas en el documento del cliente
-- ==========================================

-- Piso 1 (basado en la fila 1 de la imagen)
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('101', 1, 'Habitación Doble', 'DISPONIBLE', 2);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('102', 1, 'Habitación Simple', 'DISPONIBLE', 1);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('103', 1, 'Habitación Simple', 'DISPONIBLE', 1);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('104', 1, 'Habitación Doble', 'DISPONIBLE', 2);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('105', 1, 'Habitación Doble', 'DISPONIBLE', 2);

-- Piso 2 (basado en la fila 2 de la imagen)
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('201', 2, 'Habitación Simple', 'DISPONIBLE', 1);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('202', 2, 'Habitación Doble', 'DISPONIBLE', 2);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('203', 2, 'Habitación Simple', 'DISPONIBLE', 1);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('204', 2, 'Habitación Doble', 'DISPONIBLE', 2);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('205', 2, 'Habitación Doble', 'DISPONIBLE', 2);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('206', 2, 'Habitación Simple', 'DISPONIBLE', 1);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('207', 2, 'Habitación Simple', 'DISPONIBLE', 1);

-- Piso 3 (basado en la fila 3 de la imagen)
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('301', 3, 'Habitación Doble', 'DISPONIBLE', 2);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('302', 3, 'Habitación Doble', 'DISPONIBLE', 2);
INSERT INTO habitacion (numero, piso, descripcion, estado, id_tipo_habitacion) VALUES ('303', 3, 'Habitación Doble', 'DISPONIBLE', 2);