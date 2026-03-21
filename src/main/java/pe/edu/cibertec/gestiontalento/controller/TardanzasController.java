package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.cibertec.gestiontalento.model.Tardanzas;
import pe.edu.cibertec.gestiontalento.service.TardanzasService;

import java.util.List;

@RestController
@RequestMapping("/api/tardanzas")
public class TardanzasController {

    private final TardanzasService tardanzasService;

    @Autowired
    public TardanzasController(TardanzasService tardanzasService) {
        this.tardanzasService = tardanzasService;
    }

    @PostMapping
    public ResponseEntity<Tardanzas> crearTardanza(@RequestBody Tardanzas tardanza) {
        return new ResponseEntity<>(tardanzasService.crearTardanza(tardanza), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Tardanzas>> listarTardanzas() {
        return ResponseEntity.ok(tardanzasService.listarTardanzas());
    }

    // Nuevo: Endpoint para obtener historial por empleado
    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<Tardanzas>> listarPorEmpleado(@PathVariable int idEmpleado) {
        return ResponseEntity.ok(tardanzasService.listarPorEmpleado(idEmpleado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tardanzas> obtenerTardanzaPorId(@PathVariable int id) {
        return ResponseEntity.ok(tardanzasService.obtenerTardanzaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tardanzas> actualizarTardanza(@PathVariable int id, @RequestBody Tardanzas tardanza) {
        tardanza.setIdTardanza(id);
        return ResponseEntity.ok(tardanzasService.actualizarTardanza(tardanza));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTardanza(@PathVariable int id) {
        tardanzasService.eliminarTardanza(id);
        return ResponseEntity.noContent().build();
    }
}