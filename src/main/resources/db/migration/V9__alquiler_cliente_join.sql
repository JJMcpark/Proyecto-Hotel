-- Tabla de unión para soportar múltiples huéspedes por alquiler.
-- alquiler.id_cliente sigue siendo el representante/titular.
CREATE TABLE alquiler_cliente (
    id_alquiler INT NOT NULL,
    id_cliente  INT NOT NULL,
    PRIMARY KEY (id_alquiler, id_cliente),
    CONSTRAINT fk_ac_alquiler FOREIGN KEY (id_alquiler) REFERENCES alquiler(id)  ON DELETE CASCADE,
    CONSTRAINT fk_ac_cliente  FOREIGN KEY (id_cliente)  REFERENCES cliente(id)
);
