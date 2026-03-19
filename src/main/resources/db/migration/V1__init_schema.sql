-- ==========================================
-- 1. TABLAS DE CONFIGURACIÓN (CATÁLOGOS)
-- ==========================================

CREATE TABLE rol (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL, -- ROLE_ADMIN, ROLE_RECEPCIONISTA
    descripcion VARCHAR(255)
);

CREATE TABLE tipo_documento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL -- DNI, CE, PASAPORTE
);

CREATE TABLE tipo_habitacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL -- SIMPLE, MATRIMONIAL, DOBLE
);

CREATE TABLE tipo_alquiler (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL -- POR HORA, POR DIA, POR NOCHE
);

-- ==========================================
-- 2. IDENTIDAD Y SEGURIDAD (EL OBSERVADOR)
-- ==========================================

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    num_documento VARCHAR(20) UNIQUE NOT NULL, -- Este será el LOGIN (DNI)
    contrasena VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    id_tipo_documento INT NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento(id),
    FOREIGN KEY (id_rol) REFERENCES rol(id)
);

CREATE TABLE refresh_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    is_logged_out BOOLEAN DEFAULT FALSE,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    num_documento VARCHAR(20) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    id_tipo_documento INT NOT NULL,
    FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento(id)
);

-- ==========================================
-- 3. INFRAESTRUCTURA Y PRECIOS
-- ==========================================

CREATE TABLE habitacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(10) NOT NULL UNIQUE,
    piso INT NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'DISPONIBLE', -- DISPONIBLE, OCUPADA, LIMPIEZA
    id_tipo_habitacion INT NOT NULL,
    FOREIGN KEY (id_tipo_habitacion) REFERENCES tipo_habitacion(id)
);

CREATE TABLE tarifa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    precio DECIMAL(10, 2) NOT NULL,
    id_tipo_habitacion INT NOT NULL,
    id_tipo_alquiler INT NOT NULL,
    FOREIGN KEY (id_tipo_habitacion) REFERENCES tipo_habitacion(id),
    FOREIGN KEY (id_tipo_alquiler) REFERENCES tipo_alquiler(id)
);

-- ==========================================
-- 4. EL MICRO-UNIVERSO (OPERACIONES)
-- ==========================================

CREATE TABLE alquiler (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha_ingreso DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_prevista DATETIME NOT NULL,
    fecha_salida DATETIME NULL,
    precio_fijado DECIMAL(10, 2) NOT NULL, -- SNAPSHOT para evitar mutaciones
    cant_tiempo INT NOT NULL,             -- Cantidad de horas/días
    pago_pendiente DECIMAL(10, 2) NOT NULL, -- Total - Adelanto (si hubiera)
    estado VARCHAR(20) NOT NULL,          -- ACTIVO, FINALIZADO (Usa el Enum)
    id_cliente INT NOT NULL,
    id_habitacion INT NOT NULL,
    id_tarifa INT NOT NULL,               -- De dónde vino el precio
    id_usuario INT NOT NULL,              -- EL OBSERVADOR (Quién hizo el check-in)
    FOREIGN KEY (id_cliente) REFERENCES cliente(id),
    FOREIGN KEY (id_habitacion) REFERENCES habitacion(id),
    FOREIGN KEY (id_tarifa) REFERENCES tarifa(id),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

CREATE TABLE cuenta_alquiler (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    precio_unit DECIMAL(10, 2) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    sub_total DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(20) NOT NULL,          -- PENDIENTE, PAGADO (Usa el Enum)
    id_alquiler INT NOT NULL,
    FOREIGN KEY (id_alquiler) REFERENCES alquiler(id) ON DELETE CASCADE
);

-- ==========================================
-- 5. CAJA Y REPORTES (EL RESULTADO)
-- ==========================================

CREATE TABLE movimiento_caja (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(10) NOT NULL,            -- INGRESO, EGRESO (Usa el Enum)
    monto DECIMAL(10, 2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,     -- YAPE, EFECTIVO, etc (Usa el Enum)
    concepto VARCHAR(255) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,              -- Quién recibió/sacó la plata
    id_alquiler INT NULL,                 -- Opcional, referencia al alquiler asociado
    FOREIGN KEY (id_usuario) REFERENCES usuario(id),
    FOREIGN KEY (id_alquiler) REFERENCES alquiler(id)
);
