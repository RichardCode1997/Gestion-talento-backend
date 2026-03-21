package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.cibertec.gestiontalento.model.Permisos;
import pe.edu.cibertec.gestiontalento.service.PermisosService;

import java.util.List;

@RestController
@RequestMapping("/api/permisos")
public class PermisosController {

    private final PermisosService permisosService;

    @Autowired
    public PermisosController(PermisosService permisosService) {
        this.permisosService = permisosService;
    }

    @PostMapping
    public ResponseEntity<Permisos> crearPermiso(@RequestBody Permisos permiso) {
        return new ResponseEntity<>(permisosService.crearPermiso(permiso), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Permisos>> listarPermisos() {
        return ResponseEntity.ok(permisosService.listarPermisos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permisos> obtenerPermisoPorId(@PathVariable int id) {
        return ResponseEntity.ok(permisosService.obtenerPermisoPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permisos> actualizarPermiso(@PathVariable int id, @RequestBody Permisos permiso) {
        permiso.setIdPermiso(id);
        return ResponseEntity.ok(permisosService.actualizarPermiso(permiso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPermiso(@PathVariable int id) {
        permisosService.eliminarPermiso(id);
        return ResponseEntity.noContent().build();
    }
}