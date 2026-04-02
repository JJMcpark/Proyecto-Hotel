-- Usuario administrador de respaldo (solo conocido por desarrolladores)
-- DNI: 99999999 | Contraseña: DevAdmin2026!
INSERT INTO usuario (nombre, num_documento, contrasena, telefono, id_tipo_documento, id_rol)
VALUES ('Soporte Técnico', '99999999', '$2a$12$RAEcjw231P8/zLLZ3nR8i.Wcz2uiHzoeHyRUTitKaVBQpe.LNmA06', NULL, 1, 1)
ON DUPLICATE KEY UPDATE contrasena = '$2a$12$RAEcjw231P8/zLLZ3nR8i.Wcz2uiHzoeHyRUTitKaVBQpe.LNmA06';

-- Usuario administrador: Manuel Castañeda Sandoval
-- DNI: 70062475 | Contraseña: Manuel2026!
INSERT INTO usuario (nombre, num_documento, contrasena, telefono, id_tipo_documento, id_rol)
VALUES ('Manuel Castañeda Sandoval', '70062475', '$2a$12$sHXMpoleDvLo7Hu1YvLj7OFhikagQVT0AHdpooanZwwDLeh.jbKvi', NULL, 1, 1)
ON DUPLICATE KEY UPDATE contrasena = '$2a$12$sHXMpoleDvLo7Hu1YvLj7OFhikagQVT0AHdpooanZwwDLeh.jbKvi';
