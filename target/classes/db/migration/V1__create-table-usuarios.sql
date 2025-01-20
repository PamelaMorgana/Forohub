CREATE TABLE usuarios (
            id BIGINT NOT NULL AUTO_INCREMENT,
            nombre VARCHAR(100) NOT NULL,
            email VARCHAR(100) NOT NULL UNIQUE,
            clave VARCHAR(255) NOT NULL,
            perfil VARCHAR(100) NOT NULL,
            fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
            estado ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO',

            PRIMARY KEY(id)
        );