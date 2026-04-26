package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.gestiontalento.dtos.EmpleadoResponseDTO;
import pe.edu.cibertec.gestiontalento.model.Empleados;
import pe.edu.cibertec.gestiontalento.model.EstadoEmpleado;
import pe.edu.cibertec.gestiontalento.service.EmpleadosService;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadosController {

    private final EmpleadosService empleadosService;

    @Autowired
    public EmpleadosController(EmpleadosService empleadosService) {
        this.empleadosService = empleadosService;
    }

    // ── LISTADO ──────────────────────────────────────────────────────────────

    // Listado paginado — devuelve DTO con correoUsuario y rolUsuario.
    // SUPERADMIN es filtrado en el service antes de llegar al frontend.
    @GetMapping
    public ResponseEntity<Page<EmpleadoResponseDTO>> listarEmpleados(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(empleadosService.listarEmpleados(pageable));
    }

    // Listado filtrado por estado — también devuelve DTO y filtra SUPERADMIN.
    @GetMapping("/filtrar")
    public ResponseEntity<List<EmpleadoResponseDTO>> listarPorEstado(
            @RequestParam EstadoEmpleado estado) {
        return ResponseEntity.ok(empleadosService.listarPorEstado(estado));
    }

    // Obtener por ID — devuelve DTO enriquecido con datos del usuario.
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDTO> obtenerEmpleadoPorId(@PathVariable int id) {
        return ResponseEntity.ok(empleadosService.obtenerEmpleadoPorIdDTO(id));
    }

    // Obtener por DNI — devuelve entidad directa (usado internamente / Postman).
    @GetMapping("/dni/{dni}")
    public ResponseEntity<Empleados> obtenerPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(empleadosService.obtenerPorDni(dni));
    }

    // Empleados sin usuario vinculado — para el combobox del formulario de usuarios.
    @GetMapping("/sin-usuario")
    public ResponseEntity<List<Empleados>> listarSinUsuario() {
        return ResponseEntity.ok(empleadosService.listarSinUsuario());
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    // Crear empleado — devuelve entidad directa (no necesita DTO en la respuesta de creación).
    @PostMapping
    public ResponseEntity<Empleados> crearEmpleado(
            @RequestBody Empleados empleado,
            Authentication authentication) {
        return new ResponseEntity<>(
                empleadosService.crearEmpleado(empleado, authentication.getName()),
                HttpStatus.CREATED);
    }

    // Actualizar empleado — devuelve entidad directa.
    @PutMapping("/{id}")
    public ResponseEntity<Empleados> actualizarEmpleado(
            @PathVariable int id,
            @RequestBody Empleados empleado,
            Authentication authentication) {
        return ResponseEntity.ok(
                empleadosService.actualizarEmpleado(id, empleado, authentication.getName()));
    }

    // Cambio de estado (borrado lógico) — PATCH porque solo modifica un campo.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable int id,
            @RequestParam EstadoEmpleado estado,
            Authentication authentication) {
        empleadosService.cambiarEstadoEmpleado(id, estado, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // Eliminar (borrado físico) — también borra el usuario vinculado si existe.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpleado(
            @PathVariable int id,
            Authentication authentication) {
        empleadosService.eliminarEmpleado(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}