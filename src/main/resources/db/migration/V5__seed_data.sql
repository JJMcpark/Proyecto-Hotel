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
