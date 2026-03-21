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
-- Table `bd_api_asistencias_sama`.`horarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`horarios` (
  `id_horario` INT(11) NOT NULL AUTO_INCREMENT,
  `hora_entrada` VARCHAR(255) NULL DEFAULT NULL,
  `hora_salida` VARCHAR(255) NULL DEFAULT NULL,
  `nombre` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_horario`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `bd_api_asistencias_sama`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`roles` (
  `id_rol` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_rol` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_rol`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `bd_api_asistencias_sama`.`usuarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`usuarios` (
  `id_usuario` INT(11) NOT NULL AUTO_INCREMENT,
  `correo` VARCHAR(255) NULL DEFAULT NULL,
  `contraseña` VARCHAR(255) NULL DEFAULT NULL,
  `id_rol` INT(11) NULL DEFAULT NULL,
  `estado` TINYINT(1) NOT NULL DEFAULT 1, -- NUEVA COLUMNA: 1=Activo, 0=Inactivo
  PRIMARY KEY (`id_usuario`),
  INDEX `FK3kl77pehgupicftwfreqnjkll` (`id_rol` ASC) VISIBLE,
  CONSTRAINT `FK3kl77pehgupicftwfreqnjkll`
    FOREIGN KEY (`id_rol`)
    REFERENCES `bd_api_asistencias_sama`.`roles` (`id_rol`)
) ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `bd_api_asistencias_sama`.`departamentos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`departamentos` (
  `id_departamento` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_departamento` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_departamento`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `bd_api_asistencias_sama`.`empleados`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`empleados` (
  `id_empleado` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NULL DEFAULT NULL,
  `apellido` VARCHAR(255) NULL DEFAULT NULL,
  `dni` VARCHAR(20) NULL DEFAULT NULL,
  `celular` VARCHAR(20) NULL DEFAULT NULL,
  `direccion` VARCHAR(255) NULL DEFAULT NULL,
  `id_departamento` INT(11) NULL DEFAULT NULL,
  `id_horario` INT(11) NULL DEFAULT NULL,
  `id_usuario` INT(11) NULL DEFAULT NULL,
  `estado` TINYINT(1) NOT NULL DEFAULT 1, -- NUEVA COLUMNA: 1=Activo, 0=Inactivo
  PRIMARY KEY (`id_empleado`),
  INDEX `FKrnmesv4tqvbufe58d30ygfest` (`id_departamento` ASC) VISIBLE,
  INDEX `FKewg8142mw1isfqjugpcs0bt7c` (`id_horario` ASC) VISIBLE,
  INDEX `FKq96wltg8ec5n3u91jq4ecdnai` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `FKewg8142mw1isfqjugpcs0bt7c`
    FOREIGN KEY (`id_horario`)
    REFERENCES `bd_api_asistencias_sama`.`horarios` (`id_horario`),
  CONSTRAINT `FKq96wltg8ec5n3u91jq4ecdnai`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `bd_api_asistencias_sama`.`usuarios` (`id_usuario`),
  CONSTRAINT `FKrnmesv4tqvbufe58d30ygfest`
    FOREIGN KEY (`id_departamento`)
    REFERENCES `bd_api_asistencias_sama`.`departamentos` (`id_departamento`)
) ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `bd_api_asistencias_sama`.`asistencias`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`asistencias` (
  `id_asistencia` INT(11) NOT NULL AUTO_INCREMENT,
  `fecha` VARCHAR(255) NULL DEFAULT NULL,
  `hora_entrada` VARCHAR(255) NULL DEFAULT NULL,
  `hora_salida` VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id_asistencia`),
  INDEX `FKhovyoatphnlh8b9lj9ut6t901` (`id_empleado` ASC) VISIBLE,
  CONSTRAINT `FKhovyoatphnlh8b9lj9ut6t901`
    FOREIGN KEY (`id_empleado`)
    REFERENCES `bd_api_asistencias_sama`.`empleados` (`id_empleado`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `bd_api_asistencias_sama`.`faltas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`faltas` (
  `id_falta` INT(11) NOT NULL AUTO_INCREMENT,
  `fecha` VARCHAR(255) NULL DEFAULT NULL,
  `motivo` VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id_falta`),
  INDEX `FKkrfh5s1a08vso79of3dgpqpnw` (`id_empleado` ASC) VISIBLE,
  CONSTRAINT `FKkrfh5s1a08vso79of3dgpqpnw`
    FOREIGN KEY (`id_empleado`)
    REFERENCES `bd_api_asistencias_sama`.`empleados` (`id_empleado`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `bd_api_asistencias_sama`.`permisos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`permisos` (
  `id_permiso` INT(11) NOT NULL AUTO_INCREMENT,
  `estado` VARCHAR(255) NULL DEFAULT NULL,
  `fecha_fin` VARCHAR(255) NULL DEFAULT NULL,
  `fecha_inicio` VARCHAR(255) NULL DEFAULT NULL,
  `motivo` VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id_permiso`),
  INDEX `FK8311xma9irtcef2p2cccaguyp` (`id_empleado` ASC) VISIBLE,
  CONSTRAINT `FK8311xma9irtcef2p2cccaguyp`
    FOREIGN KEY (`id_empleado`)
    REFERENCES `bd_api_asistencias_sama`.`empleados` (`id_empleado`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `bd_api_asistencias_sama`.`tardanzas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`tardanzas` (
  `id_tardanza` INT(11) NOT NULL AUTO_INCREMENT,
  `fecha` VARCHAR(255) NULL DEFAULT NULL,
  `hora_tardanza` VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id_tardanza`),
  INDEX `FK9rn83luwmel5u5i2x5grevq4v` (`id_empleado` ASC) VISIBLE,
  CONSTRAINT `FK9rn83luwmel5u5i2x5grevq4v`
    FOREIGN KEY (`id_empleado`)
    REFERENCES `bd_api_asistencias_sama`.`empleados` (`id_empleado`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4;

-- -----------------------------------------------------
-- Table `bd_api_asistencias_sama`.`noticias`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_api_asistencias_sama`.`noticias` (
  `id_noticias` INT(11) NOT NULL AUTO_INCREMENT,
  `noticia_detalle` VARCHAR(255) NULL DEFAULT NULL,
  `estado` VARCHAR(255) NULL DEFAULT NULL,
  `noticia_titulo` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_noticias`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;