-- 1. ELIMINACIÓN Y CREACIÓN DEL ESQUEMA

DROP DATABASE IF EXISTS `bd_api_asistencias_sama`;
CREATE SCHEMA IF NOT EXISTS `bd_api_asistencias_sama` DEFAULT CHARACTER SET utf8mb4 ;
USE `bd_api_asistencias_sama` ;

-- 2. CONFIGURACIONES DE SESIÓN
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- 3. CREACIÓN DE TABLAS

-- -----------------------------------------------------
-- Table `horarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`horarios` (
  `id_horario`    INT(11)      NOT NULL AUTO_INCREMENT,
  `hora_entrada`  VARCHAR(255) NULL DEFAULT NULL,
  `hora_salida`   VARCHAR(255) NULL DEFAULT NULL,
  `nombre`        VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_horario`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`roles` (
  `id_rol`     INT(11)      NOT NULL AUTO_INCREMENT,
  `nombre_rol` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_rol`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `departamentos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`departamentos` (
  `id_departamento`     INT(11)      NOT NULL AUTO_INCREMENT,
  `nombre_departamento` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_departamento`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `empleados`
-- NOTA: ya NO tiene id_usuario. El empleado existe independientemente.
-- Un empleado puede o no tener una cuenta de acceso (usuario).
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`empleados` (
  `id_empleado`    INT(11)      NOT NULL AUTO_INCREMENT,
  `nombre`         VARCHAR(255) NULL DEFAULT NULL,
  `apellido`       VARCHAR(255) NULL DEFAULT NULL,
  `dni`            VARCHAR(20)  NULL DEFAULT NULL,
  `celular`        VARCHAR(20)  NULL DEFAULT NULL,
  `direccion`      VARCHAR(255) NULL DEFAULT NULL,
  `id_departamento` INT(11)     NULL DEFAULT NULL,
  `id_horario`     INT(11)      NULL DEFAULT NULL,
  `estado`         ENUM('Activo','Inactivo','Cesado') NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_empleado`),
  INDEX `FK_empleados_departamento` (`id_departamento` ASC),
  INDEX `FK_empleados_horario`      (`id_horario` ASC),
  CONSTRAINT `FK_empleados_departamento`
    FOREIGN KEY (`id_departamento`)
    REFERENCES `bd_api_asistencias_sama`.`departamentos` (`id_departamento`),
  CONSTRAINT `FK_empleados_horario`
    FOREIGN KEY (`id_horario`)
    REFERENCES `bd_api_asistencias_sama`.`horarios` (`id_horario`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `usuarios`
-- NOTA: ahora tiene id_empleado (NOT NULL, UNIQUE).
-- Un usuario SIEMPRE debe estar vinculado a un empleado.
-- La relación es 1:1 — un empleado solo puede tener un usuario.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`usuarios` (
  `id_usuario`  INT(11)      NOT NULL AUTO_INCREMENT,
  `correo`      VARCHAR(255) NULL DEFAULT NULL,
  `contraseña`  VARCHAR(255) NULL DEFAULT NULL,
  `estado`      TINYINT(1)   NOT NULL DEFAULT 1,
  `id_rol`      INT(11)      NOT NULL,
  `id_empleado` INT(11)      NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `UQ_usuarios_empleado` (`id_empleado` ASC),
  INDEX `FK_usuarios_rol`      (`id_rol` ASC),
  CONSTRAINT `FK_usuarios_rol`
    FOREIGN KEY (`id_rol`)
    REFERENCES `bd_api_asistencias_sama`.`roles` (`id_rol`),
  CONSTRAINT `FK_usuarios_empleado`
    FOREIGN KEY (`id_empleado`)
    REFERENCES `bd_api_asistencias_sama`.`empleados` (`id_empleado`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `asistencias`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`asistencias` (
  `id_asistencia` INT(11)      NOT NULL AUTO_INCREMENT,
  `fecha`         VARCHAR(255) NULL DEFAULT NULL,
  `hora_entrada`  VARCHAR(255) NULL DEFAULT NULL,
  `hora_salida`   VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado`   INT(11)      NULL DEFAULT NULL,
  PRIMARY KEY (`id_asistencia`),
  INDEX `FK_asistencias_empleado` (`id_empleado` ASC),
  CONSTRAINT `FK_asistencias_empleado`
    FOREIGN KEY (`id_empleado`)
    REFERENCES `bd_api_asistencias_sama`.`empleados` (`id_empleado`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `faltas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`faltas` (
  `id_falta`   INT(11)      NOT NULL AUTO_INCREMENT,
  `fecha`      VARCHAR(255) NULL DEFAULT NULL,
  `motivo`     VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado` INT(11)     NULL DEFAULT NULL,
  PRIMARY KEY (`id_falta`),
  INDEX `FK_faltas_empleado` (`id_empleado` ASC),
  CONSTRAINT `FK_faltas_empleado`
    FOREIGN KEY (`id_empleado`)
    REFERENCES `bd_api_asistencias_sama`.`empleados` (`id_empleado`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `permisos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`permisos` (
  `id_permiso`   INT(11)      NOT NULL AUTO_INCREMENT,
  `estado`       VARCHAR(255) NULL DEFAULT NULL,
  `fecha_fin`    VARCHAR(255) NULL DEFAULT NULL,
  `fecha_inicio` VARCHAR(255) NULL DEFAULT NULL,
  `motivo`       VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado`  INT(11)      NULL DEFAULT NULL,
  PRIMARY KEY (`id_permiso`),
  INDEX `FK_permisos_empleado` (`id_empleado` ASC),
  CONSTRAINT `FK_permisos_empleado`
    FOREIGN KEY (`id_empleado`)
    REFERENCES `bd_api_asistencias_sama`.`empleados` (`id_empleado`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `tardanzas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`tardanzas` (
  `id_tardanza`  INT(11)      NOT NULL AUTO_INCREMENT,
  `fecha`        VARCHAR(255) NULL DEFAULT NULL,
  `hora_tardanza` VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado`  INT(11)      NULL DEFAULT NULL,
  PRIMARY KEY (`id_tardanza`),
  INDEX `FK_tardanzas_empleado` (`id_empleado` ASC),
  CONSTRAINT `FK_tardanzas_empleado`
    FOREIGN KEY (`id_empleado`)
    REFERENCES `bd_api_asistencias_sama`.`empleados` (`id_empleado`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `noticias`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`noticias` (
  `id_noticias`     INT(11)      NOT NULL AUTO_INCREMENT,
  `noticia_detalle` VARCHAR(255) NULL DEFAULT NULL,
  `estado`          VARCHAR(255) NULL DEFAULT NULL,
  `noticia_titulo`  VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_noticias`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;