# Gestión de Talento - Backend 🏢

API REST robusta para la administración de Recursos Humanos, diseñada para automatizar el control de asistencia, incidencias y comunicación interna.

## 🌐 Demo

[![Ver Demo](https://img.shields.io/badge/Ver%20Demo-Live-brightgreen?style=for-the-badge)](https://gestion-talento-frontend-swart.vercel.app)

**Credenciales de prueba:**
- Correo: `richard@gmail.com`
- Contraseña: `123`

---

## 🚀 Módulos y Funcionalidades

### 🛡️ Seguridad y Usuarios
* **Autenticación JWT (Stateless):** Validación de credenciales mediante tokens firmados con Spring Security.
* **Gestión de Usuarios:** CRUD completo con contraseñas encriptadas (BCrypt).

### 👥 Gestión de Personal y Horarios
* **Empleados:** Administración completa de perfiles de trabajadores.
* **Horarios:** CRUD para la configuración de jornadas laborales.

### ⏱️ Control de Asistencia e Incidencias
* **Asistencias:** Seguimiento de registros de entrada/salida.
* **Tardanzas:** Control y reporte automático de ingresos fuera de hora.
* **Faltas y Permisos:** Gestión de inasistencias y solicitudes de permisos.

### 📢 Comunicación
* **Noticias:** CRUD de comunicados y avisos corporativos.

---

## ✅ Reglas de Negocio y Seguridad

* **DNI y Celular Único:** Bloqueo de registros duplicados.
* **Relación 1:1 Usuario-Empleado:** Cada cuenta pertenece a un único trabajador. Al eliminar un usuario, el empleado queda desvinculado automáticamente.
* **Protección de Administradores:** No se puede desactivar, cesar, ni eliminar a un empleado o usuario con rol ADMINISTRADOR desde la aplicación. Solo un DBA puede hacerlo directamente en la base de datos.
* **Autoprotección:** Un administrador no puede modificar ni eliminar su propia cuenta.
* **Activación bloqueada:** No se puede activar el usuario de un empleado cesado.
* **Integridad Referencial:** Control de excepciones para llaves foráneas (Departamentos, Horarios).

---

## 🛠️ Correr en local

### Requisitos
* Java 17
* MySQL 8
* Maven

### 1. Clonar la rama de desarrollo
```bash
git clone -b dev https://github.com/RichardCode1997/Gestion-talento-backend.git
```

### 2. Base de Datos
Los scripts están en la carpeta `/sql`. Ejecútalos en este orden en MySQL Workbench:
1. **`schema.sql`** — Crea la base de datos, tablas y relaciones.
2. **`data.sql`** — Carga datos maestros (Roles, Departamentos, Horarios) y registros de prueba.

### 3. Configuración
En `src/main/resources/application.properties` actualiza tus credenciales:
```properties
# mysql
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/bd_api_asistencias_sama?useSSL=false&serverTimeZone=UTC
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# Configurar JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true

# Perfil de desarrollo
spring.profiles.active=dev
```

### 4. Ejecutar
```bash
./mvnw spring-boot:run
```

### 5. Pruebas con Postman
En la carpeta `/postman` encontrarás la colección lista para importar.
1. Importa `Gestion Talento API.postman_collection.json`.
2. Crea un environment con `base_url = http://localhost:8080`.
3. Haz Login para obtener tu token — se aplica automáticamente a todos los endpoints.

---

## 🚧 En Desarrollo

Los siguientes módulos están disponibles en la API pero aún en proceso de integración con el frontend:

* **Horarios** — Configuración de jornadas laborales
* **Asistencias** — Registro de entradas y salidas
* **Tardanzas** — Control de ingresos fuera de hora
* **Faltas** — Gestión de inasistencias
* **Permisos** — Solicitudes de permisos justificados
* **Noticias** — Comunicados corporativos

---
## 🛠️ Stack Tecnológico

| Tecnología | Uso |
|---|---|
| Java 17 / Spring Boot 3 | Framework principal |
| Spring Security + JWT | Autenticación y autorización |
| Spring Data JPA | Persistencia de datos |
| MySQL | Base de datos |
| Lombok | Reducción de boilerplate |
| Maven | Gestión de dependencias |

---

## 🔗 Repositorio Frontend
👉 [Gestion-talento-frontend](https://github.com/RichardCode1997/Gestion-talento-frontend)
