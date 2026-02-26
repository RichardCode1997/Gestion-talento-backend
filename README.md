# Gestión de Talento - Backend 🏢

API REST robusta para la administración de Recursos Humanos, diseñada para automatizar el control de asistencia, incidencias y comunicación interna.

## 🚀 Módulos y Funcionalidades

### 🛡️ Seguridad y Usuarios
* **Autenticación:** Implementación de **JWT (Stateless)** y seguridad con **Spring Security**.
* **Gestión de Usuarios:** CRUD completo de cuentas de acceso al sistema con contraseñas encriptadas (**BCrypt**).

### 👥 Gestión de Personal y Horarios
* **Empleados:** Listado y administración de perfiles de trabajadores.
* **Horarios:** CRUD para la configuración de jornadas laborales.

### ⏱️ Control de Asistencia e Incidencias
* **Asistencias:** Seguimiento y listado de registros de entrada/salida.
* **Tardanzas:** Control y reporte automático de ingresos fuera de hora.
* **Faltas y Permisos:** Gestión y listado de inasistencias y flujo de solicitudes de permisos.

### 📢 Comunicación
* **Noticias:** CRUD de comunicados y avisos corporativos para los trabajadores.

## 🛠️ Stack Tecnológico
* **Java 17 / Spring Boot 3**
* **Spring Security**
* **Spring Data JPA**
* **MySQL**
* **Lombok**