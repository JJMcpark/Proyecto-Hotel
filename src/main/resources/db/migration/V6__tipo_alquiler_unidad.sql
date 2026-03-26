-- ============================================================
-- V6: Agregar campos unidad y multiplicador a tipo_alquiler
-- Permite calcular fechaPrevista de forma genérica:
--   fechaPrevista = now + cantTiempo * multiplicador (en unidad)
-- ============================================================

ALTER TABLE tipo_alquiler
    ADD COLUMN unidad VARCHAR(10) NOT NULL DEFAULT 'DIA',
    ADD COLUMN multiplicador INT NOT NULL DEFAULT 1;

-- Migrar datos existentes
UPDATE tipo_alquiler SET unidad = 'HORA', multiplicador = 1 WHERE nombre = 'POR HORA';
UPDATE tipo_alquiler SET unidad = 'DIA',  multiplicador = 1 WHERE nombre = 'POR DIA';
UPDATE tipo_alquiler SET unidad = 'DIA',  multiplicador = 1 WHERE nombre = 'POR NOCHE';
UPDATE tipo_alquiler SET unidad = 'DIA',  multiplicador = 7 WHERE nombre = 'POR SEMANA';
