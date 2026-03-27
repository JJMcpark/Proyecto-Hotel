-- ==========================================
-- 1. TABLAS DE CONFIGURACIÓN (CATÁLOGOS)
-- ==========================================

INSERT INTO rol (nombre, descripcion) VALUES 
('ROLE_ADMINISTRADOR', 'Administrador del sistema'),
('ROLE_RECEPCIONISTA', 'Personal de recepción');

INSERT INTO tipo_documento (nombre) VALUES 
('DNI'),
('CE'),
('PASAPORTE');

-- Solo las que me confirmaste que existen
INSERT INTO tipo_habitacion (nombre) VALUES 
('SIMPLE'),
('DOBLE');

INSERT INTO tipo_alquiler (nombre) VALUES 
('POR HORA'),
('POR DIA'),
('POR NOCHE');

-- ==========================================
-- 2. USUARIOS BASE (El Observador)
-- ==========================================

-- Admin Base (Asignado al rol 1 y tipo_doc 1)
INSERT INTO usuario (nombre, num_documento, contrasena, telefono, id_tipo_documento, id_rol) VALUES 
('Administrador', '00000000', '$2a$12$0yQpSYp.7mbWHl5Gg3WwReu8WQn/dhXtrEPY.UrFLbGKCnepJ0EiO', NULL, 1, 1); 

-- Recepcionista Base (Asignado al rol 2 y tipo_doc 1)
INSERT INTO usuario (nombre, num_documento, contrasena, telefono, id_tipo_documento, id_rol) VALUES 
('Recepcionista', '11111111', '$2a$12$3dH7qqbiYqLb412RJ4GnWuGumjzdNtFeUArowHkEnhA9Fu7WQJu52', NULL, 1, 2);