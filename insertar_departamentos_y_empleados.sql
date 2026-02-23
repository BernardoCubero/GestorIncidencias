-- ============================================================================
-- Insertar DEPARTAMENTOS y EMPLEADOS
-- ============================================================================

USE gestion_incidencias;

-- ============================================================================
-- PASO 1: Insertar DEPARTAMENTOS (si no existen)
-- ============================================================================

INSERT IGNORE INTO departamentos (id, nombre) VALUES
(1, 'Tecnología'),
(2, 'Soporte Técnico'),
(3, 'Recursos Humanos'),
(4, 'Administración');

SELECT 'Departamentos verificados' AS Estado;
SELECT * FROM departamentos;

-- ============================================================================
-- PASO 2: Insertar EMPLEADOS
-- ============================================================================

-- Tecnología (departamento_id = 1)
INSERT INTO empleados (nombre, departamento_id) VALUES ('Laura Fernández', 1);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Roberto Martín', 1);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Elena Sánchez', 1);
INSERT INTO empleados (nombre, departamento_id) VALUES ('David Rodríguez', 1);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Patricia González', 1);

-- Soporte Técnico (departamento_id = 2)
INSERT INTO empleados (nombre, departamento_id) VALUES ('Miguel Ángel Torres', 2);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Carmen Ruiz', 2);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Francisco Jiménez', 2);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Isabel Moreno', 2);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Javier Álvarez', 2);

-- Recursos Humanos (departamento_id = 3)
INSERT INTO empleados (nombre, departamento_id) VALUES ('Beatriz Romero', 3);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Antonio Navarro', 3);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Cristina Delgado', 3);

-- Administración (departamento_id = 4)
INSERT INTO empleados (nombre, departamento_id) VALUES ('Sergio Ortiz', 4);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Marta Molina', 4);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Raúl Castro', 4);
INSERT INTO empleados (nombre, departamento_id) VALUES ('Silvia Rubio', 4);

