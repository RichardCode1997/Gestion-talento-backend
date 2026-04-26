USE `bd_api_asistencias_sama`;

/* 1. LIMPIEZA TOTAL */
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

TRUNCATE TABLE tardanzas;
TRUNCATE TABLE permisos;
TRUNCATE TABLE faltas;
TRUNCATE TABLE asistencias;
TRUNCATE TABLE noticias;
TRUNCATE TABLE usuarios;     -- usuarios antes que empleados (FK usuarios.id_empleado)
TRUNCATE TABLE empleados;
TRUNCATE TABLE departamentos;
TRUNCATE TABLE horarios;
TRUNCATE TABLE roles;

/* 2. CATÁLOGOS BASE */

INSERT INTO roles (nombre_rol) VALUES 
('SUPERADMIN'), ('ADMINISTRADOR'), ('SUPERVISOR'), ('ASESOR'), ('SISTEMAS');

INSERT INTO horarios (hora_entrada, hora_salida, nombre) VALUES
('08:00', '18:00', 'Horario Diurno'),
('20:00', '06:00', 'Horario Nocturno'),
('19:00', '01:00', 'Horario Especial'),
('17:00', '20:00', 'Horario Sobre Tiempo');

INSERT INTO departamentos (nombre_departamento) VALUES 
('Ventas'), ('Recursos Humanos'), ('Finanzas');

/* 3. EMPLEADOS GÉNESIS
   Se insertan PRIMERO porque usuarios ahora apunta a empleados.
   Sin usuario vinculado aún — eso viene después. */

INSERT INTO empleados (nombre, apellido, dni, celular, direccion, id_departamento, id_horario, estado)
SELECT 'Super', 'Admin', '00000000', '000000000', 'Sistema',
    d.id_departamento, h.id_horario, 'Activo'
FROM departamentos d, horarios h
WHERE d.nombre_departamento = 'Recursos Humanos'
  AND h.nombre = 'Horario Diurno';

INSERT INTO empleados (nombre, apellido, dni, celular, direccion, id_departamento, id_horario, estado)
SELECT 'Richard', 'Quispe', '12345678', '123456789', 'Calle abc mz abc lt.abc',
    d.id_departamento, h.id_horario, 'Activo'
FROM departamentos d, horarios h
WHERE d.nombre_departamento = 'Ventas'
  AND h.nombre = 'Horario Diurno';

/* 4. USUARIOS GÉNESIS
   Ahora se insertan DESPUÉS del empleado, vinculando por DNI (valor único). */

INSERT INTO usuarios (correo, contraseña, id_rol, id_empleado, estado)
SELECT 
    'superadmin@gestiontalento.com',
    '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.',
    (SELECT id_rol FROM roles WHERE nombre_rol = 'SUPERADMIN'),
    e.id_empleado,
    1
FROM empleados e WHERE e.dni = '00000000';

INSERT INTO usuarios (correo, contraseña, id_rol, id_empleado, estado)
SELECT 
    'richard@gmail.com',
    '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.',
    (SELECT id_rol FROM roles WHERE nombre_rol = 'ADMINISTRADOR'),
    e.id_empleado,
    1
FROM empleados e WHERE e.dni = '12345678';

/* 5. TABLAS OPERATIVAS */

-- ASISTENCIAS — join por DNI (ya no necesitamos pasar por usuarios)
INSERT INTO asistencias (fecha, hora_entrada, hora_salida, id_empleado)
SELECT '2023-06-26', '09:00:00', '18:00:00', e.id_empleado
FROM empleados e WHERE e.dni = '12345678';

-- FALTAS
INSERT INTO faltas (fecha, motivo, id_empleado)
SELECT '2023-06-26', 'Motivo de la falta', e.id_empleado
FROM empleados e WHERE e.dni = '12345678';

-- PERMISOS
INSERT INTO permisos (estado, fecha_inicio, fecha_fin, motivo, id_empleado)
SELECT 'Aprobado', '2023-06-25', '2023-06-28', 'Motivo del permiso', e.id_empleado
FROM empleados e WHERE e.dni = '12345678';

-- TARDANZAS
INSERT INTO tardanzas (fecha, hora_tardanza, id_empleado)
SELECT '2023-06-26', '10:15:00', e.id_empleado
FROM empleados e WHERE e.dni = '12345678';

/* 6. NOTICIAS */
INSERT INTO noticias (noticia_detalle, estado, noticia_titulo) VALUES
('Por semana santa tendremos libre desde el jueves 25 hasta el Lunes 29', 'Activo', 'FERIADO LARGO'),
('Para el Turno mañana, tener presente que la hora de ingreso es a las 08:00 am', 'Activo', 'DESCUENTOS POR TARDANZAS'),
('Saludamos a sus familiares de parte de los lideres de la Empresa', 'Desactivado', 'FELIZ NAVIDAD');

/* 7. EMPLEADOS GENÉRICOS (20)
   Se insertan primero sin usuario. */

INSERT INTO empleados (nombre, apellido, dni, celular, direccion, id_departamento, id_horario, estado) VALUES
('Empleado1',  'Test', '11111111', '911111111', 'Direccion 1',  1, 1, 'Activo'),
('Empleado2',  'Test', '11111112', '911111112', 'Direccion 2',  1, 1, 'Activo'),
('Empleado3',  'Test', '11111113', '911111113', 'Direccion 3',  1, 1, 'Activo'),
('Empleado4',  'Test', '11111114', '911111114', 'Direccion 4',  1, 1, 'Activo'),
('Empleado5',  'Test', '11111115', '911111115', 'Direccion 5',  1, 1, 'Activo'),
('Empleado6',  'Test', '11111116', '911111116', 'Direccion 6',  1, 1, 'Activo'),
('Empleado7',  'Test', '11111117', '911111117', 'Direccion 7',  1, 1, 'Activo'),
('Empleado8',  'Test', '11111118', '911111118', 'Direccion 8',  1, 1, 'Activo'),
('Empleado9',  'Test', '11111119', '911111119', 'Direccion 9',  1, 1, 'Activo'),
('Empleado10', 'Test', '11111120', '911111120', 'Direccion 10', 1, 1, 'Activo'),
('Empleado11', 'Test', '11111121', '911111121', 'Direccion 11', 1, 1, 'Activo'),
('Empleado12', 'Test', '11111122', '911111122', 'Direccion 12', 1, 1, 'Activo'),
('Empleado13', 'Test', '11111123', '911111123', 'Direccion 13', 1, 1, 'Activo'),
('Empleado14', 'Test', '11111124', '911111124', 'Direccion 14', 1, 1, 'Activo'),
('Empleado15', 'Test', '11111125', '911111125', 'Direccion 15', 1, 1, 'Activo'),
('Empleado16', 'Test', '11111126', '911111126', 'Direccion 16', 1, 1, 'Activo'),
('Empleado17', 'Test', '11111127', '911111127', 'Direccion 17', 1, 1, 'Activo'),
('Empleado18', 'Test', '11111128', '911111128', 'Direccion 18', 1, 1, 'Activo'),
('Empleado19', 'Test', '11111129', '911111129', 'Direccion 19', 1, 1, 'Activo'),
('Empleado20', 'Test', '11111130', '911111130', 'Direccion 20', 1, 1, 'Activo');

/* 8. USUARIOS GENÉRICOS (20)
   Se insertan después, vinculando a cada empleado por DNI. */

INSERT INTO usuarios (correo, contraseña, id_rol, id_empleado, estado)
SELECT 'usuario_t1@test.com',  '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'),    e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111111' UNION ALL
SELECT 'usuario_t2@test.com',  '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111112' UNION ALL
SELECT 'usuario_t3@test.com',  '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'),    e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111113' UNION ALL
SELECT 'usuario_t4@test.com',  '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111114' UNION ALL
SELECT 'usuario_t5@test.com',  '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'),    e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111115' UNION ALL
SELECT 'usuario_t6@test.com',  '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111116' UNION ALL
SELECT 'usuario_t7@test.com',  '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'),    e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111117' UNION ALL
SELECT 'usuario_t8@test.com',  '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111118' UNION ALL
SELECT 'usuario_t9@test.com',  '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'),    e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111119' UNION ALL
SELECT 'usuario_t10@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111120' UNION ALL
SELECT 'usuario_t11@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'),    e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111121' UNION ALL
SELECT 'usuario_t12@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111122' UNION ALL
SELECT 'usuario_t13@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'),    e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111123' UNION ALL
SELECT 'usuario_t14@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111124' UNION ALL
SELECT 'usuario_t15@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'),    e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111125' UNION ALL
SELECT 'usuario_t16@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111126' UNION ALL
SELECT 'usuario_t17@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'),    e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111127' UNION ALL
SELECT 'usuario_t18@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111128' UNION ALL
SELECT 'usuario_t19@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'),    e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111129' UNION ALL
SELECT 'usuario_t20@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), e.id_empleado, 1 FROM empleados e WHERE e.dni = '11111130';

/* 9. REACTIVAR PROTECCIONES */
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

/* SELECTS DE VERIFICACIÓN */
SELECT * FROM bd_api_asistencias_sama.roles;
SELECT * FROM bd_api_asistencias_sama.horarios;
SELECT * FROM bd_api_asistencias_sama.departamentos;
SELECT * FROM bd_api_asistencias_sama.empleados;
SELECT * FROM bd_api_asistencias_sama.usuarios;
SELECT * FROM bd_api_asistencias_sama.asistencias;
SELECT * FROM bd_api_asistencias_sama.faltas;
SELECT * FROM bd_api_asistencias_sama.permisos;
SELECT * FROM bd_api_asistencias_sama.tardanzas;
SELECT * FROM bd_api_asistencias_sama.noticias;