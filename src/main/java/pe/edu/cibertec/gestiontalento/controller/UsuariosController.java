package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.cibertec.gestiontalento.model.Usuarios;
import pe.edu.cibertec.gestiontalento.service.UsuariosService;
import org.springframework.security.core.Authentication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    public ResponseEntity<Usuarios> crearUsuario(@RequestBody Usuarios usuario, Authentication authentication) {
        return new ResponseEntity<>(usuariosService.crearUsuario(usuario, authentication.getName()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Usuarios>> listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(usuariosService.listarUsuarios(pageable));
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
    public ResponseEntity<Usuarios> modificarUsuario(@PathVariable int id, @RequestBody Usuarios usuarioModificado, Authentication authentication) {
        return ResponseEntity.ok(usuariosService.modificarUsuario(id, usuarioModificado,authentication.getName()));
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
    public ResponseEntity<Void> activarUsuario(@PathVariable int id, Authentication authentication) {
        usuariosService.activarUsuario(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}