
DROP DATABASE IF EXISTS gestion_incidencias;
CREATE DATABASE gestion_incidencias;
USE gestion_incidencias;




-- Tabla: departamentos
CREATE TABLE departamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: empleados
CREATE TABLE empleados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    departamento_id BIGINT NOT NULL,
    CONSTRAINT fk_empleado_departamento FOREIGN KEY (departamento_id) 
        REFERENCES departamentos(id) ON DELETE RESTRICT ON UPDATE CASCADE
)

-- Tabla: incidencias_relacionales
CREATE TABLE incidencias_relacionales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    fechaCreacion DATETIME NOT NULL,
    nombreCliente VARCHAR(150) NOT NULL,
    telefonoCliente VARCHAR(20),
    emailCliente VARCHAR(100),
    estado VARCHAR(50),
    empleado_id BIGINT NOT NULL,
    CONSTRAINT fk_incidencia_empleado FOREIGN KEY (empleado_id) 
        REFERENCES empleados(id) ON DELETE RESTRICT ON UPDATE CASCADE
)



INSERT INTO departamentos (id, nombre) VALUES
(1, 'Tecnología'),
(2, 'Soporte Técnico'),
(3, 'Recursos Humanos'),
(4, 'Administración');


INSERT INTO empleados (nombre, departamento_id) VALUES 
('Laura Fernández', 1),
('Roberto Martín', 1),
('Elena Sánchez', 1),
('David Rodríguez', 1),
('Patricia González', 1);


INSERT INTO empleados (nombre, departamento_id) VALUES 
('Miguel Ángel Torres', 2),
('Carmen Ruiz', 2),
('Francisco Jiménez', 2),
('Isabel Moreno', 2),
('Javier Álvarez', 2);


INSERT INTO empleados (nombre, departamento_id) VALUES 
('Beatriz Romero', 3),
('Antonio Navarro', 3),
('Cristina Delgado', 3);


INSERT INTO empleados (nombre, departamento_id) VALUES 
('Sergio Ortiz', 4),
('Marta Molina', 4),
('Raúl Castro', 4),
('Silvia Rubio', 4);
