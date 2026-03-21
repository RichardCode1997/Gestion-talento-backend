package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.cibertec.gestiontalento.model.Horarios;
import pe.edu.cibertec.gestiontalento.service.HorariosService;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorariosController {

    private final HorariosService horariosService;

    @Autowired
    public HorariosController(HorariosService horariosService) {
        this.horariosService = horariosService;
    }

    @PostMapping
    public ResponseEntity<Horarios> crearHorario(@RequestBody Horarios horario) {
        return new ResponseEntity<>(horariosService.crearHorario(horario), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Horarios>> listarHorarios() {
        return ResponseEntity.ok(horariosService.listarHorarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Horarios> obtenerHorarioPorId(@PathVariable int id) {
        return ResponseEntity.ok(horariosService.obtenerHorarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Horarios> actualizarHorario(@PathVariable int id, @RequestBody Horarios horario) {
        horario.setIdHorario(id);
        return ResponseEntity.ok(horariosService.actualizarHorario(horario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable int id) {
        horariosService.eliminarHorario(id);
        return ResponseEntity.noContent().build();
    }
}