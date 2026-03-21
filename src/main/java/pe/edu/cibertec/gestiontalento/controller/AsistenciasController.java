package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.gestiontalento.model.Asistencias;
import pe.edu.cibertec.gestiontalento.service.AsistenciasService;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciasController {

    private final AsistenciasService asistenciasService;

    @Autowired
    public AsistenciasController(AsistenciasService asistenciasService) {
        this.asistenciasService = asistenciasService;
    }

    @PostMapping
    public ResponseEntity<Asistencias> crearAsistencia(@RequestBody Asistencias asistencia) {
        return new ResponseEntity<>(asistenciasService.crearAsistencia(asistencia), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Asistencias>> listarAsistencias() {
        return ResponseEntity.ok(asistenciasService.listarAsistencias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asistencias> obtenerAsistenciaPorId(@PathVariable int id) {
        return ResponseEntity.ok(asistenciasService.obtenerAsistenciaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asistencias> actualizarAsistencia(@PathVariable int id, @RequestBody Asistencias asistencia) {
        asistencia.setIdAsistencia(id); // Aseguramos que se actualice el registro correcto
        return ResponseEntity.ok(asistenciasService.actualizarAsistencia(asistencia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsistencia(@PathVariable int id) {
        asistenciasService.eliminarAsistencia(id);
        return ResponseEntity.noContent().build();
    }
}