package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.cibertec.gestiontalento.model.Usuarios;
import pe.edu.cibertec.gestiontalento.service.UsuariosService;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    private final UsuariosService usuariosService;

    @Autowired
    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    @PostMapping
    public ResponseEntity<Usuarios> crearUsuario(@RequestBody Usuarios usuario) {
        return new ResponseEntity<>(usuariosService.crearUsuario(usuario), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Usuarios>> listarUsuarios() {
        return ResponseEntity.ok(usuariosService.listarUsuarios());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Usuarios>> listarActivos() {
        return ResponseEntity.ok(usuariosService.listarUsuariosActivos());
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<Usuarios>> listarSoloInactivos() {
        List<Usuarios> inactivos = usuariosService.listarUsuariosInactivos();
        return ResponseEntity.ok(inactivos);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Usuarios> buscarPorCorreo(@RequestParam String correo) {
        return ResponseEntity.ok(usuariosService.obtenerPorCorreo(correo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> obtenerUsuarioPorId(@PathVariable int id) {
        return ResponseEntity.ok(usuariosService.obtenerUsuarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuarios> modificarUsuario(@PathVariable int id, @RequestBody Usuarios usuarioModificado) {
        return ResponseEntity.ok(usuariosService.modificarUsuario(id, usuarioModificado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable int id, Authentication authentication) {
        usuariosService.eliminarUsuario(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // Bloqueo de acceso (Solo cambia estado a 0)
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable int id, Authentication authentication) {
        usuariosService.desactivarUsuario(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // Desbloqueo de acceso (Solo cambia estado a 1)
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarUsuario(@PathVariable int id) {
        usuariosService.activarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}