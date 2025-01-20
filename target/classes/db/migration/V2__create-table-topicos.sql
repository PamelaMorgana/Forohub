CREATE TABLE topicos (
          id BIGINT NOT NULL AUTO_INCREMENT,
          titulo VARCHAR(255) NOT NULL,
          mensaje TEXT NOT NULL,
          fecha_de_creacion DATETIME NOT NULL,
          fecha_actualizacion DATETIME,
          status ENUM('ACTIVO', 'INACTIVO', 'ELIMINADO') DEFAULT 'ACTIVO',
          curso VARCHAR(100),
          usuario_id BIGINT NOT NULL,

          PRIMARY KEY(id),
          CONSTRAINT fk_topicos_usuarios_id FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
);