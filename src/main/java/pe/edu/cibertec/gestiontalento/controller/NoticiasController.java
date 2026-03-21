package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.gestiontalento.model.Noticias;
import pe.edu.cibertec.gestiontalento.service.NoticiasService;

import java.util.List;

@RestController
@RequestMapping("/api/noticias")
public class NoticiasController {

    private final NoticiasService noticiasService;

    @Autowired
    public NoticiasController(NoticiasService noticiasService) {
        this.noticiasService = noticiasService;
    }

    @PostMapping
    public ResponseEntity<Noticias> crearNoticia(@RequestBody Noticias noticias) {
        return new ResponseEntity<>(noticiasService.crearNoticia(noticias), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Noticias>> listarNoticias() {
        return ResponseEntity.ok(noticiasService.listarNoticias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Noticias> obtenerNoticiaPorId(@PathVariable int id) {
        return ResponseEntity.ok(noticiasService.obtenerNoticiaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Noticias> actualizarNoticia(@PathVariable int id, @RequestBody Noticias noticias) {
        noticias.setIdNoticia(id);
        return ResponseEntity.ok(noticiasService.actualizarNoticia(noticias));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNoticia(@PathVariable int id) {
        noticiasService.eliminarNoticia(id);
        return ResponseEntity.noContent().build();
    }
}