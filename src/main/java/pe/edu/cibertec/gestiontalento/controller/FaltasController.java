package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.cibertec.gestiontalento.model.Faltas;
import pe.edu.cibertec.gestiontalento.service.FaltasService;

import java.util.List;

@RestController
@RequestMapping("/api/faltas")
public class FaltasController {

    private final FaltasService faltasService;

    @Autowired
    public FaltasController(FaltasService faltasService) {
        this.faltasService = faltasService;
    }

    @PostMapping
    public ResponseEntity<Faltas> crearFalta(@RequestBody Faltas falta) {
        return new ResponseEntity<>(faltasService.crearFalta(falta), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Faltas>> listarFaltas() {
        return ResponseEntity.ok(faltasService.listarFaltas());
    }

    // Nuevo: Endpoint para ver faltas de un empleado específico
    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<Faltas>> listarPorEmpleado(@PathVariable int idEmpleado) {
        return ResponseEntity.ok(faltasService.listarPorEmpleado(idEmpleado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faltas> obtenerFaltaPorId(@PathVariable int id) {
        return ResponseEntity.ok(faltasService.obtenerFaltaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faltas> actualizarFalta(@PathVariable int id, @RequestBody Faltas falta) {
        falta.setIdFalta(id);
        return ResponseEntity.ok(faltasService.actualizarFalta(falta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFalta(@PathVariable int id) {
        faltasService.eliminarFalta(id);
        return ResponseEntity.noContent().build();
    }
}