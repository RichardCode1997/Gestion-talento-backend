package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.cibertec.gestiontalento.model.Roles;
import pe.edu.cibertec.gestiontalento.service.RolesService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolesController {

    private final RolesService rolesService;

    @Autowired
    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @PostMapping
    public ResponseEntity<Roles> crearRol(@RequestBody Roles rol) {
        return new ResponseEntity<>(rolesService.crearRol(rol), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Roles>> listarRoles() {
        return ResponseEntity.ok(rolesService.listarRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Roles> obtenerRolPorId(@PathVariable int id) {
        return ResponseEntity.ok(rolesService.obtenerRolPorId(id));
    }

    // Nuevo: Buscar por nombre de rol (ej: ROLE_ADMIN)
    @GetMapping("/nombre/{nombreRol}")
    public ResponseEntity<Roles> obtenerRolPorNombre(@PathVariable String nombreRol) {
        return ResponseEntity.ok(rolesService.obtenerRolPorNombre(nombreRol));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Roles> actualizarRol(@PathVariable int id, @RequestBody Roles rol) {
        rol.setIdRol(id);
        return ResponseEntity.ok(rolesService.actualizarRol(rol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable int id) {
        rolesService.eliminarRol(id);
        return ResponseEntity.noContent().build();
    }
}