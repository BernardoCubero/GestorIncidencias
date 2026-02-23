-- Script de inicialización para Gestión de Incidencias
-- Base de Datos: MySQL

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS gestion_incidencias
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE gestion_incidencias;

-- Las tablas se crean automáticamente con Hibernate (hbm2ddl.auto=update)
-- Este script es solo para datos de prueba

-- Insertar departamentos de ejemplo
INSERT IGNORE INTO departamentos (id, nombre) VALUES
(1, 'Tecnología'),
(2, 'Soporte Técnico'),
(3, 'Recursos Humanos'),
(4, 'Administración');

-- Insertar empleados de ejemplo
INSERT IGNORE INTO empleados (id, nombre, departamento_id) VALUES
(1, 'Juan Pérez', 1),
(2, 'María García', 1),
(3, 'Carlos López', 2),
(4, 'Ana Martínez', 2),
(5, 'Pedro Sánchez', 3);

-- Insertar incidencias de ejemplo
INSERT IGNORE INTO incidencias_relacionales (id, titulo, fechaCreacion, nombreCliente, telefonoCliente, emailCliente, empleado_id) VALUES
(1, 'Error en servidor de aplicaciones', '2024-01-15 10:30:00', 'Juan García López', '612345678', 'juan.garcia@email.com', 1),
(2, 'Solicitud de acceso VPN', '2024-01-16 14:20:00', 'María Rodríguez', '623456789', 'maria.r@empresa.com', 2),
(3, 'Problema con impresora', '2024-01-17 09:15:00', 'Carlos Pérez', '634567890', NULL, 3),
(4, 'Actualización de software', '2024-01-18 11:45:00', 'Ana Martínez Silva', NULL, 'ana.martinez@company.es', 1),
(5, 'Configuración de correo', '2024-01-19 16:00:00', 'Pedro Sánchez', '656789012', 'pedro.s@mail.com', 4);

-- Verificar los datos insertados
SELECT 'Departamentos insertados:' as '';
SELECT * FROM departamentos;

SELECT 'Empleados insertados:' as '';
SELECT * FROM empleados;

SELECT 'Incidencias insertadas:' as '';
SELECT * FROM incidencias_relacionales;

-- Consulta de verificación de relaciones
SELECT 
    i.id as incidencia_id,
    i.titulo,
    i.nombreCliente,
    i.telefonoCliente,
    i.emailCliente,
    i.fechaCreacion,
    e.nombre as empleado,
    d.nombre as departamento
FROM incidencias_relacionales i
JOIN empleados e ON i.empleado_id = e.id
JOIN departamentos d ON e.departamento_id = d.id
ORDER BY i.fechaCreacion DESC;
