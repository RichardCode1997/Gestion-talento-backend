USE `railway`;

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

/* 2. ROLES */
INSERT INTO roles (nombre_rol) VALUES
('ADMINISTRADOR'), ('SUPERVISOR'), ('ASESOR'), ('SISTEMAS');

/* 3. HORARIOS */
INSERT INTO `horarios` (`hora_entrada`, `hora_salida`, `nombre`) VALUES
('08:00', '18:00', 'Horario Diurno'),
('20:00', '06:00', 'Horario Nocturno'),
('19:00', '01:00', 'Horario Especial'),
('17:00', '20:00', 'Horario Sobre Tiempo');

/* 4. DEPARTAMENTOS */
INSERT INTO `departamentos` (`nombre_departamento`) VALUES
('Ventas'), ('Recursos Humanos'), ('Finanzas');

/* 5. USUARIOS */
INSERT INTO usuarios (correo, contraseña, id_rol, estado)
SELECT 'richard@gmail.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', r.id_rol, 1
FROM roles r WHERE r.nombre_rol = 'ADMINISTRADOR';

/* 6. EMPLEADOS */
INSERT INTO empleados (nombre, apellido, dni, celular, direccion, id_departamento, id_horario, id_usuario, estado)
SELECT 'Richard', 'Quispe', '12345678', '999888777', 'Calle abc mz abc lt.abc',
    d.id_departamento, h.id_horario, u.id_usuario, 'Activo'
FROM departamentos d, horarios h, usuarios u
WHERE d.nombre_departamento = 'Ventas'
  AND h.nombre = 'Horario Diurno'
  AND u.correo = 'richard@gmail.com';

/* 7. OPERACIONES */
INSERT INTO asistencias (fecha, hora_entrada, hora_salida, id_empleado)
SELECT '2023-06-26', '09:00:00', '18:00:00', e.id_empleado
FROM empleados e JOIN usuarios u ON e.id_usuario = u.id_usuario
WHERE u.correo = 'richard@gmail.com';

INSERT INTO faltas (fecha, motivo, id_empleado)
SELECT '2023-06-26', 'Motivo de la falta', e.id_empleado
FROM empleados e WHERE e.dni = '12345678';

INSERT INTO permisos (estado, fecha_inicio, fecha_fin, motivo, id_empleado)
SELECT 'Aprobado', '2023-06-25', '2023-06-28', 'Motivo del permiso', e.id_empleado
FROM empleados e WHERE e.dni = '12345678';

INSERT INTO tardanzas (fecha, hora_tardanza, id_empleado)
SELECT '2023-06-26', '10:15:00', e.id_empleado
FROM empleados e WHERE e.dni = '12345678';

/* 8. NOTICIAS */
INSERT INTO `noticias` (`noticia_detalle`, `estado`, `noticia_titulo`) VALUES
('Por semana santa tendremos libre desde el jueves 25 hasta el Lunes 29', 'Activo', 'FERIADO LARGO'),
('Para el Turno mañana, tener presente que la hora de ingreso es a las 08:00 am', 'Activo', 'DESCUENTOS POR TARDANZAS'),
('Saludamos a sus familiares de parte de los lideres de la Empresa', 'Desactivado', 'FELIZ NAVIDAD');

/* 9. REACTIVAR PROTECCIONES */
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

/* VERIFICACIÓN */
SELECT * FROM roles;
SELECT * FROM empleados;
SELECT * FROM noticias;
SELECT * FROM usuarios;
