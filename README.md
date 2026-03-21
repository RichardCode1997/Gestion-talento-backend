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

### ✅ Validaciones y Reglas de Negocio
Se han implementado restricciones a nivel de Service para garantizar la integridad de los datos:
* **DNI y Celular Único:** Bloqueo de registros duplicados para evitar conflictos de identidad.
* **Relación 1:1 Usuario-Empleado:** Garantiza que cada cuenta de acceso pertenezca a un único trabajador.
* **Integridad Referencial:** Control de excepciones para llaves foráneas (Departamentos, Horarios).

## 🛠️ Instalación y Configuración

###  Base de Datos 🗄️
Los scripts de configuración se encuentran en la carpeta `/sql`. Para levantar el entorno correctamente, ejecútalos en tu gestor de MySQL (Workbench, por ejemplo) en el siguiente orden:

1.  **`schema.sql`**: Crea la base de datos `bd_api_asistencias_sama`, las tablas y las relaciones (Foreign Keys).
2.  **`data.sql`**: Carga los datos maestros necesarios (Roles, Departamentos, Horarios) y registros de prueba.

> **Nota:** No olvides actualizar las credenciales de tu base de datos en el archivo `src/main/resources/application.properties`.

### 🚀 Pruebas con Postman
En la carpeta `/postman` del repositorio encontrarás la colección lista para importar.
1. Importa el archivo `Gestion Talento API.postman_collection.json`.
2. Realiza el **Login** para obtener tu token.
3. El token se aplica automáticamente a todos los endpoints mediante la herencia de autenticación (Bearer Token).

## 🛠️ Stack Tecnológico
* **Java 17 / Spring Boot 3**
* **Spring Security**
* **Spring Data JPA**
* **MySQL**
* **Lombok**