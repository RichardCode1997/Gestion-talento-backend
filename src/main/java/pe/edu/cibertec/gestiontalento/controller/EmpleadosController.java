package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.cibertec.gestiontalento.model.Empleados;
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

    // --- LISTADO ---

    @GetMapping
    public ResponseEntity<List<Empleados>> listarTodos() {
        return ResponseEntity.ok(empleadosService.listarEmpleados());
    }

    // SOLO muestra a los empleados activos
    @GetMapping("/activos")
    public ResponseEntity<List<Empleados>> listarEmpleadosActivos() {
        return ResponseEntity.ok(empleadosService.listarSoloActivos());
    }

    // 2. La "Papelera" o Histórico (Solo los que fueron desactivados)
    @GetMapping("/inactivos")
    public ResponseEntity<List<Empleados>> listarEmpleadosInactivos() {
        // Asegúrate de haber creado este metodo en el Service
        return ResponseEntity.ok(empleadosService.listarSoloInactivos());
    }

    // 3. Listado por dni
    @GetMapping("/dni/{dni}")
    public ResponseEntity<Empleados> obtenerPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(empleadosService.obtenerPorDni(dni));
    }

    // --- OPERACIONES DE BORRADO / DESACTIVACIÓN ---

    // 1. DESACTIVAR (Borrado Lógico) - Usamos PATCH porque solo modificamos un campo (el estado)
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarEmpleado(@PathVariable int id) {
        empleadosService.desactivarEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    // 1. ACTIVAR - Usamos PATCH porque solo modificamos un campo (el estado)
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarEmpleado(@PathVariable int id) {
        empleadosService.activarEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    // 3. ELIMINAR (Borrado Físico) - El que ya tenías, que borra de la DB
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

    @PutMapping("/{id}")
    public ResponseEntity<Empleados> actualizarEmpleado(@PathVariable int id, @RequestBody Empleados empleado) {
        Empleados actualizado = empleadosService.actualizarEmpleado(id, empleado);
        return ResponseEntity.ok(actualizado);
    }
}