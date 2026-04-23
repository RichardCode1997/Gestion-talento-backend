package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.cibertec.gestiontalento.model.Empleados;
import pe.edu.cibertec.gestiontalento.model.EstadoEmpleado;
import pe.edu.cibertec.gestiontalento.service.EmpleadosService;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadosController {

    private final EmpleadosService empleadosService;

    @Autowired
    public EmpleadosController(EmpleadosService empleadosService) {
        this.empleadosService = empleadosService;
    }

    // --- LISTADO ---

    @GetMapping
    public ResponseEntity<List<Empleados>> listarTodos() {
        return ResponseEntity.ok(empleadosService.listarEmpleados());
    }

    // Muestra a los empleados
    @GetMapping("/filtrar")
    public ResponseEntity<List<Empleados>> listarPorEstado(@RequestParam EstadoEmpleado estado) {
        return ResponseEntity.ok(empleadosService.listarPorEstado(estado));
    }

    // 3. Listado por dni
    @GetMapping("/dni/{dni}")
    public ResponseEntity<Empleados> obtenerPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(empleadosService.obtenerPorDni(dni));
    }

    // --- OPERACIONES DE BORRADO / CAMBIO DE ESTADO ---

    // 1. CAMBIO DE ESTADO (Borrado Lógico) - Usamos PATCH porque solo modificamos un campo (el estado)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable int id,
            @RequestParam EstadoEmpleado estado,
            Authentication authentication) {
        String correoLogueado = authentication.getName();
        empleadosService.cambiarEstadoEmpleado(id, estado, correoLogueado);
        return ResponseEntity.noContent().build();
    }

    // 2. ELIMINAR (Borrado Físico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable int id) {
        empleadosService.eliminarEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    // --- RESTO DE MÉTODOS ---

    @PostMapping
    public ResponseEntity<Empleados> crearEmpleado(@RequestBody Empleados empleado) {
        return new ResponseEntity<>(empleadosService.crearEmpleado(empleado), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleados> obtenerEmpleadoPorId(@PathVariable int id) {
        return ResponseEntity.ok(empleadosService.obtenerEmpleadoPorId(id));
    }

    @GetMapping("/sin-usuario")
    public ResponseEntity<List<Empleados>> listarSinUsuario() {
        return ResponseEntity.ok(empleadosService.listarSinUsuario());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleados> actualizarEmpleado(@PathVariable int id, @RequestBody Empleados empleado) {
        Empleados actualizado = empleadosService.actualizarEmpleado(id, empleado);
        return ResponseEntity.ok(actualizado);
    }
}