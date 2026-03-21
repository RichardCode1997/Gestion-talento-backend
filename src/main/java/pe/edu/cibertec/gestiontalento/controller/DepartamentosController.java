package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.cibertec.gestiontalento.model.Departamentos;
import pe.edu.cibertec.gestiontalento.service.DepartamentosService;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentosController {

    private final DepartamentosService departamentosService;

    @Autowired
    public DepartamentosController(DepartamentosService departamentosService) {
        this.departamentosService = departamentosService;
    }

    @PostMapping
    public ResponseEntity<Departamentos> crearDepartamento(@RequestBody Departamentos departamento) {
        return new ResponseEntity<>(departamentosService.crearDepartamento(departamento), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Departamentos>> listarDepartamentos() {
        return ResponseEntity.ok(departamentosService.listarDepartamentos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departamentos> obtenerDepartamentoPorId(@PathVariable int id) {
        return ResponseEntity.ok(departamentosService.obtenerDepartamentoPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departamentos> actualizarDepartamento(@PathVariable int id, @RequestBody Departamentos departamento) {
        departamento.setIdDepartamento(id);
        return ResponseEntity.ok(departamentosService.actualizarDepartamento(departamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDepartamento(@PathVariable int id) {
        departamentosService.eliminarDepartamento(id);
        return ResponseEntity.noContent().build();
    }
}