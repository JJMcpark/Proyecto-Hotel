-- ==========================================
-- V5__seed_data.sql
-- Datos base para operación inicial
-- ==========================================

-- ==========================================
-- 1. TARIFAS (SIMPLE=1, DOBLE=2 | POR HORA=1, POR DIA=2, POR NOCHE=3)
-- ==========================================

INSERT INTO tarifa (precio, id_tipo_habitacion, id_tipo_alquiler) VALUES (25.00, 1, 1);  -- SIMPLE  x POR HORA
INSERT INTO tarifa (precio, id_tipo_habitacion, id_tipo_alquiler) VALUES (80.00, 1, 2);  -- SIMPLE  x POR DIA
INSERT INTO tarifa (precio, id_tipo_habitacion, id_tipo_alquiler) VALUES (60.00, 1, 3);  -- SIMPLE  x POR NOCHE
INSERT INTO tarifa (precio, id_tipo_habitacion, id_tipo_alquiler) VALUES (35.00, 2, 1);  -- DOBLE   x POR HORA
INSERT INTO tarifa (precio, id_tipo_habitacion, id_tipo_alquiler) VALUES (120.00, 2, 2); -- DOBLE   x POR DIA
INSERT INTO tarifa (precio, id_tipo_habitacion, id_tipo_alquiler) VALUES (90.00, 2, 3);  -- DOBLE   x POR NOCHE

-- ==========================================
-- 2. EMPRESAS DE EJEMPLO
-- ==========================================

INSERT INTO empresa (nombre, ruc, telefono) VALUES ('Empresa Demo S.A.C.', '20123456789', '014567890');
INSERT INTO empresa (nombre, ruc, telefono) VALUES ('Transportes Perú E.I.R.L.', '20987654321', '016543210');

-- ==========================================
-- 3. CLIENTES DE EJEMPLO
-- ==========================================

INSERT INTO cliente (nombre, num_documento, telefono, id_tipo_documento, id_empresa) VALUES ('Juan Pérez García', '72345678', '987654321', 1, NULL);
INSERT INTO cliente (nombre, num_documento, telefono, id_tipo_documento, id_empresa) VALUES ('María López Ríos', '45678901', '912345678', 1, 1);
INSERT INTO cliente (nombre, num_documento, telefono, id_tipo_documento, id_empresa) VALUES ('Carlos Mendoza Ruiz', 'CE202501', '956781234', 2, NULL);
