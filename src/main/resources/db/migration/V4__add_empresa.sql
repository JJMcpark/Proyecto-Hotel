-- ==========================================
-- V4__add_empresa.sql
-- Agrega tabla empresa y FK opcionales en cliente y alquiler
-- ==========================================

CREATE TABLE empresa (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    ruc        CHAR(11)     NOT NULL UNIQUE,
    telefono   VARCHAR(20)
);

ALTER TABLE cliente
    ADD COLUMN id_empresa BIGINT NULL,
    ADD CONSTRAINT fk_cliente_empresa
        FOREIGN KEY (id_empresa) REFERENCES empresa(id)
        ON DELETE SET NULL;

ALTER TABLE alquiler
    ADD COLUMN id_empresa BIGINT NULL,
    ADD CONSTRAINT fk_alquiler_empresa
        FOREIGN KEY (id_empresa) REFERENCES empresa(id)
        ON DELETE SET NULL;
