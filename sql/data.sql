USE `bd_api_asistencias_sama`;

/* 1. LIMPIEZA TOTAL - Desactiva protecciones y limpia tablas */
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

TRUNCATE TABLE tardanzas;
TRUNCATE TABLE permisos;
TRUNCATE TABLE faltas;
TRUNCATE TABLE asistencias;
TRUNCATE TABLE noticias;
TRUNCATE TABLE empleados;
TRUNCATE TABLE usuarios;
TRUNCATE TABLE departamentos;
TRUNCATE TABLE horarios;
TRUNCATE TABLE roles;

/* 2. ROLES - Catálogo base */
INSERT INTO roles (nombre_rol) VALUES 
('SUPERADMIN'), ('ADMINISTRADOR'), ('SUPERVISOR'), ('ASESOR'), ('SISTEMAS');

/* 3. HORARIOS - Catálogo base */
INSERT INTO `horarios` (`hora_entrada`, `hora_salida`, `nombre`) VALUES
('08:00', '18:00', 'Horario Diurno'),
('20:00', '06:00', 'Horario Nocturno'),
('19:00', '01:00', 'Horario Especial'),
('17:00', '20:00', 'Horario Sobre Tiempo');

/* 4. DEPARTAMENTOS - Catálogo base */
INSERT INTO `departamentos` (`nombre_departamento`) VALUES 
('Ventas'), ('Recursos Humanos'), ('Finanzas');

/* 5. USUARIOS - Genesis */
-- Se usa el ID del rol buscando por nombre para evitar errores

INSERT INTO usuarios (correo, contraseña, id_rol, estado)
SELECT 'superadmin@gestiontalento.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', r.id_rol, 1
FROM roles r WHERE r.nombre_rol = 'SUPERADMIN';

INSERT INTO usuarios (correo, contraseña, id_rol, estado)
SELECT 'richard@gmail.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', r.id_rol, 1
FROM roles r WHERE r.nombre_rol = 'ADMINISTRADOR';

/* 6. EMPLEADOS - Genesis */
-- Aquí vinculamos todo usando nombres y correos, no IDs manuales

INSERT INTO empleados (nombre, apellido, dni, celular, direccion, id_departamento, id_horario, id_usuario, estado)
SELECT 'Super', 'Admin', '00000000', '000000000', 'Sistema',
    d.id_departamento, h.id_horario, u.id_usuario, 'Activo'
FROM departamentos d, horarios h, usuarios u
WHERE d.nombre_departamento = 'Recursos Humanos'
  AND h.nombre = 'Horario Diurno'
  AND u.correo = 'superadmin@gestiontalento.com';

INSERT INTO empleados (nombre, apellido, dni, celular, direccion, id_departamento, id_horario, id_usuario, estado) -- -- Activo, Inactivo, Cesado
SELECT 
    'Richard', 'Quispe', '12345678', '123456789', 'Calle abc mz abc lt.abc',
    d.id_departamento, 
    h.id_horario, 
    u.id_usuario,
    'Activo'
FROM departamentos d, horarios h, usuarios u
WHERE d.nombre_departamento = 'Ventas' 
  AND h.nombre = 'Horario Diurno' 
  AND u.correo = 'richard@gmail.com';

/* 7. TABLAS OPERATIVAS (Asistencias, Faltas, etc.) */
-- La mejor práctica es usar JOIN con la tabla de usuarios/empleados filtrando por un valor UNIQUE (Correo o DNI)

-- ASISTENCIAS
INSERT INTO asistencias (fecha, hora_entrada, hora_salida, id_empleado)
SELECT '2023-06-26', '09:00:00', '18:00:00', e.id_empleado
FROM empleados e JOIN usuarios u ON e.id_usuario = u.id_usuario
WHERE u.correo = 'richard@gmail.com';

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

/* 8. NOTICIAS - Independiente */
INSERT INTO `noticias` (`noticia_detalle`, `estado`, `noticia_titulo`) VALUES
('Por semana santa tendremos libre desde el jueves 25 hasta el Lunes 29', 'Activo', 'FERIADO LARGO'),
('Para el Turno mañana, tener presente que la hora de ingreso es a las 08:00 am', 'Activo', 'DESCUENTOS POR TARDANZAS'),
('Saludamos a sus familiares de parte de los lideres de la Emoresa', 'Desactivado', 'FELIZ NAVIDAD');

/* 9. Usuarios y Empleados Genericos */

-- 1. Insertar 20 usuarios
INSERT INTO usuarios (correo, contraseña, id_rol, estado) VALUES
('usuario_t1@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'), 1),
('usuario_t2@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), 1),
('usuario_t3@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'), 1),
('usuario_t4@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), 1),
('usuario_t5@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'), 1),
('usuario_t6@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), 1),
('usuario_t7@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'), 1),
('usuario_t8@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), 1),
('usuario_t9@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'), 1),
('usuario_t10@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), 1),
('usuario_t11@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'), 1),
('usuario_t12@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), 1),
('usuario_t13@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'), 1),
('usuario_t14@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), 1),
('usuario_t15@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'), 1),
('usuario_t16@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), 1),
('usuario_t17@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'), 1),
('usuario_t18@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), 1),
('usuario_t19@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'ASESOR'), 1),
('usuario_t20@test.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', (SELECT id_rol FROM roles WHERE nombre_rol = 'SISTEMAS'), 1);

-- 2. Insertar 20 empleados vinculados por correo
INSERT INTO empleados (nombre, apellido, dni, celular, direccion, id_departamento, id_horario, id_usuario, estado)
SELECT 'Empleado1', 'Test', '11111111', '911111111', 'Direccion 1', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t1@test.com' UNION ALL
SELECT 'Empleado2', 'Test', '11111112', '911111112', 'Direccion 2', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t2@test.com' UNION ALL
SELECT 'Empleado3', 'Test', '11111113', '911111113', 'Direccion 3', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t3@test.com' UNION ALL
SELECT 'Empleado4', 'Test', '11111114', '911111114', 'Direccion 4', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t4@test.com' UNION ALL
SELECT 'Empleado5', 'Test', '11111115', '911111115', 'Direccion 5', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t5@test.com' UNION ALL
SELECT 'Empleado6', 'Test', '11111116', '911111116', 'Direccion 6', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t6@test.com' UNION ALL
SELECT 'Empleado7', 'Test', '11111117', '911111117', 'Direccion 7', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t7@test.com' UNION ALL
SELECT 'Empleado8', 'Test', '11111118', '911111118', 'Direccion 8', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t8@test.com' UNION ALL
SELECT 'Empleado9', 'Test', '11111119', '911111119', 'Direccion 9', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t9@test.com' UNION ALL
SELECT 'Empleado10', 'Test', '11111120', '911111120', 'Direccion 10', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t10@test.com' UNION ALL
SELECT 'Empleado11', 'Test', '11111121', '911111121', 'Direccion 11', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t11@test.com' UNION ALL
SELECT 'Empleado12', 'Test', '11111122', '911111122', 'Direccion 12', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t12@test.com' UNION ALL
SELECT 'Empleado13', 'Test', '11111123', '911111123', 'Direccion 13', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t13@test.com' UNION ALL
SELECT 'Empleado14', 'Test', '11111124', '911111124', 'Direccion 14', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t14@test.com' UNION ALL
SELECT 'Empleado15', 'Test', '11111125', '911111125', 'Direccion 15', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t15@test.com' UNION ALL
SELECT 'Empleado16', 'Test', '11111126', '911111126', 'Direccion 16', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t16@test.com' UNION ALL
SELECT 'Empleado17', 'Test', '11111127', '911111127', 'Direccion 17', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t17@test.com' UNION ALL
SELECT 'Empleado18', 'Test', '11111128', '911111128', 'Direccion 18', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t18@test.com' UNION ALL
SELECT 'Empleado19', 'Test', '11111129', '911111129', 'Direccion 19', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t19@test.com' UNION ALL
SELECT 'Empleado20', 'Test', '11111130', '911111130', 'Direccion 20', 1, 1, u.id_usuario, 'Activo' FROM usuarios u WHERE u.correo = 'usuario_t20@test.com';

/* 10. REACTIVAR PROTECCIONES */
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

/*SELECTS*/
SELECT * FROM bd_api_asistencias_sama.asistencias;
SELECT * FROM bd_api_asistencias_sama.departamentos;
SELECT * FROM bd_api_asistencias_sama.empleados;
SELECT * FROM bd_api_asistencias_sama.faltas;
SELECT * FROM bd_api_asistencias_sama.horarios;
SELECT * FROM bd_api_asistencias_sama.noticias;
SELECT * FROM bd_api_asistencias_sama.permisos;
SELECT * FROM bd_api_asistencias_sama.tardanzas;
SELECT * FROM bd_api_asistencias_sama.roles;
SELECT * FROM bd_api_asistencias_sama.usuarios;

