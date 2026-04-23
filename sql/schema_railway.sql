USE `railway`;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

CREATE TABLE IF NOT EXISTS `railway`.`horarios` (
  `id_horario` INT(11) NOT NULL AUTO_INCREMENT,
  `hora_entrada` VARCHAR(255) NULL DEFAULT NULL,
  `hora_salida` VARCHAR(255) NULL DEFAULT NULL,
  `nombre` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_horario`))
ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `railway`.`roles` (
  `id_rol` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_rol` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_rol`))
ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `railway`.`usuarios` (
  `id_usuario` INT(11) NOT NULL AUTO_INCREMENT,
  `correo` VARCHAR(255) NULL DEFAULT NULL,
  `contraseña` VARCHAR(255) NULL DEFAULT NULL,
  `id_rol` INT(11) NULL DEFAULT NULL,
  `estado` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_usuario`),
  INDEX `FK3kl77pehgupicftwfreqnjkll` (`id_rol` ASC),
  CONSTRAINT `FK3kl77pehgupicftwfreqnjkll`
    FOREIGN KEY (`id_rol`) REFERENCES `railway`.`roles` (`id_rol`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `railway`.`departamentos` (
  `id_departamento` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_departamento` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_departamento`))
ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `railway`.`empleados` (
  `id_empleado` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NULL DEFAULT NULL,
  `apellido` VARCHAR(255) NULL DEFAULT NULL,
  `dni` VARCHAR(20) NULL DEFAULT NULL,
  `celular` VARCHAR(20) NULL DEFAULT NULL,
  `direccion` VARCHAR(255) NULL DEFAULT NULL,
  `id_departamento` INT(11) NULL DEFAULT NULL,
  `id_horario` INT(11) NULL DEFAULT NULL,
  `id_usuario` INT(11) NULL DEFAULT NULL,
  `estado` VARCHAR(10) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_empleado`),
  INDEX `FKrnmesv4tqvbufe58d30ygfest` (`id_departamento` ASC),
  INDEX `FKewg8142mw1isfqjugpcs0bt7c` (`id_horario` ASC),
  INDEX `FKq96wltg8ec5n3u91jq4ecdnai` (`id_usuario` ASC),
  CONSTRAINT `FKewg8142mw1isfqjugpcs0bt7c`
    FOREIGN KEY (`id_horario`) REFERENCES `railway`.`horarios` (`id_horario`),
  CONSTRAINT `FKq96wltg8ec5n3u91jq4ecdnai`
    FOREIGN KEY (`id_usuario`) REFERENCES `railway`.`usuarios` (`id_usuario`),
  CONSTRAINT `FKrnmesv4tqvbufe58d30ygfest`
    FOREIGN KEY (`id_departamento`) REFERENCES `railway`.`departamentos` (`id_departamento`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `railway`.`asistencias` (
  `id_asistencia` INT(11) NOT NULL AUTO_INCREMENT,
  `fecha` VARCHAR(255) NULL DEFAULT NULL,
  `hora_entrada` VARCHAR(255) NULL DEFAULT NULL,
  `hora_salida` VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id_asistencia`),
  INDEX `FKhovyoatphnlh8b9lj9ut6t901` (`id_empleado` ASC),
  CONSTRAINT `FKhovyoatphnlh8b9lj9ut6t901`
    FOREIGN KEY (`id_empleado`) REFERENCES `railway`.`empleados` (`id_empleado`))
ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `railway`.`faltas` (
  `id_falta` INT(11) NOT NULL AUTO_INCREMENT,
  `fecha` VARCHAR(255) NULL DEFAULT NULL,
  `motivo` VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id_falta`),
  INDEX `FKkrfh5s1a08vso79of3dgpqpnw` (`id_empleado` ASC),
  CONSTRAINT `FKkrfh5s1a08vso79of3dgpqpnw`
    FOREIGN KEY (`id_empleado`) REFERENCES `railway`.`empleados` (`id_empleado`))
ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `railway`.`permisos` (
  `id_permiso` INT(11) NOT NULL AUTO_INCREMENT,
  `estado` VARCHAR(255) NULL DEFAULT NULL,
  `fecha_fin` VARCHAR(255) NULL DEFAULT NULL,
  `fecha_inicio` VARCHAR(255) NULL DEFAULT NULL,
  `motivo` VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id_permiso`),
  INDEX `FK8311xma9irtcef2p2cccaguyp` (`id_empleado` ASC),
  CONSTRAINT `FK8311xma9irtcef2p2cccaguyp`
    FOREIGN KEY (`id_empleado`) REFERENCES `railway`.`empleados` (`id_empleado`))
ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `railway`.`tardanzas` (
  `id_tardanza` INT(11) NOT NULL AUTO_INCREMENT,
  `fecha` VARCHAR(255) NULL DEFAULT NULL,
  `hora_tardanza` VARCHAR(255) NULL DEFAULT NULL,
  `id_empleado` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id_tardanza`),
  INDEX `FK9rn83luwmel5u5i2x5grevq4v` (`id_empleado` ASC),
  CONSTRAINT `FK9rn83luwmel5u5i2x5grevq4v`
    FOREIGN KEY (`id_empleado`) REFERENCES `railway`.`empleados` (`id_empleado`))
ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `railway`.`noticias` (
  `id_noticias` INT(11) NOT NULL AUTO_INCREMENT,
  `noticia_detalle` VARCHAR(255) NULL DEFAULT NULL,
  `estado` VARCHAR(255) NULL DEFAULT NULL,
  `noticia_titulo` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_noticias`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;