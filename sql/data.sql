USE `bd_api_asistencias_sama`;

/* 1. ROLES - Catálogo base */
INSERT INTO roles (nombre_rol) VALUES
('ADMINISTRADOR'), ('SUPERVISOR'), ('ASESOR'), ('SISTEMAS');

/* 2. HORARIOS - Catálogo base */
INSERT INTO `horarios` (`hora_entrada`, `hora_salida`, `nombre`) VALUES
('08:00', '18:00', 'Horario Diurno'),
('20:00', '06:00', 'Horario Nocturno'),
('19:00', '01:00', 'Horario Especial'),
('17:00', '20:00', 'Horario Sobre Tiempo');

/* 3. DEPARTAMENTOS - Catálogo base */
INSERT INTO `departamentos` (`nombre_departamento`) VALUES
('Ventas'), ('Recursos Humanos'), ('Finanzas');

/* 4. USUARIOS - Solo credenciales */
-- Se usa el ID del rol buscando por nombre para evitar errores
INSERT INTO usuarios (correo, contraseña, id_rol, estado) -- Agregamos 'estado' 1
SELECT 'ssericksson@gmail.com', '$2a$12$YLTI.MC1Y2htSfU3ox2TPOLQDwECywi2sibwsX1M1IAxXDqZgyku.', r.id_rol, 1
FROM roles r WHERE r.nombre_rol = 'ADMINISTRADOR';

/* 5. EMPLEADOS - Información Personal + Relaciones */
-- Aqui vinculamos usando nombres y correos, no IDs manuales
INSERT INTO empleados (nombre, apellido, dni, celular, direccion, id_departamento, id_horario, id_usuario, estado) -- Agregamos 'estado' 1
SELECT
    'Richard', 'Quispe', '12345678', '933788480', 'Calle abc mz abc lt.abc',
    d.id_departamento,
    h.id_horario,
    u.id_usuario,
    1
FROM departamentos d, horarios h, usuarios u
WHERE d.nombre_departamento = 'Ventas'
  AND h.nombre = 'Horario Diurno'
  AND u.correo = 'ssericksson@gmail.com';

/* 6. TABLAS OPERATIVAS (Asistencias, Faltas, etc.) */
-- La mejor práctica es usar JOIN con la tabla de usuarios/empleados filtrando por un valor UNIQUE (Correo o DNI)

-- ASISTENCIAS
INSERT INTO asistencias (fecha, hora_entrada, hora_salida, id_empleado)
SELECT '2023-06-26', '09:00:00', '18:00:00', e.id_empleado
FROM empleados e JOIN usuarios u ON e.id_usuario = u.id_usuario
WHERE u.correo = 'ssericksson@gmail.com';

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

/* 7. NOTICIAS - Independiente */
INSERT INTO `noticias` (`noticia_detalle`, `estado`, `noticia_titulo`) VALUES
('Por semana santa tendremos libre desde el jueves 25 hasta el Lunes 29', 'Activo', 'FERIADO LARGO'),
('Para el Turno mañana, tener presente que la hora de ingreso es a las 08:00 am', 'Activo', 'DESCUENTOS POR TARDANZAS'),
('Saludamos a sus familiares de parte de los lideres de la Emoresa', 'Desactivado', 'FELIZ NAVIDAD');



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