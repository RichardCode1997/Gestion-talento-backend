package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.gestiontalento.dtos.UsuarioResponseDTO;
import pe.edu.cibertec.gestiontalento.model.Usuarios;
import pe.edu.cibertec.gestiontalento.service.UsuariosService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    private final UsuariosService usuariosService;

    @Autowired
    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    // ── LISTADO — devuelven DTO, SUPERADMIN filtrado en el service ────────────

    // Listado paginado — filtra SUPERADMIN y aplana datos del empleado.
    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuarios(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(usuariosService.listarUsuarios(pageable));
    }

    // Solo activos — filtra SUPERADMIN.
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarActivos() {
        return ResponseEntity.ok(usuariosService.listarUsuariosActivos());
    }

    // Solo inactivos — filtra SUPERADMIN.
    @GetMapping("/inactivos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarSoloInactivos() {
        return ResponseEntity.ok(usuariosService.listarUsuariosInactivos());
    }

    // ── OPERACIONES INDIVIDUALES — devuelven entidad directa ─────────────────

    // Crear usuario — recibe entidad, devuelve entidad.
    @PostMapping
    public ResponseEntity<Usuarios> crearUsuario(
            @RequestBody Usuarios usuario,
            Authentication authentication) {
        return new ResponseEntity<>(
                usuariosService.crearUsuario(usuario, authentication.getName()),
                HttpStatus.CREATED);
    }

    // Obtener por ID — devuelve entidad directa (usado en edición).
    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> obtenerUsuarioPorId(@PathVariable int id) {
        return ResponseEntity.ok(usuariosService.obtenerUsuarioPorId(id));
    }

    // Buscar por correo — devuelve entidad directa (usado internamente / login).
    @GetMapping("/buscar")
    public ResponseEntity<Usuarios> buscarPorCorreo(@RequestParam String correo) {
        return ResponseEntity.ok(usuariosService.obtenerPorCorreo(correo));
    }

    // Modificar — recibe entidad, devuelve entidad.
    @PutMapping("/{id}")
    public ResponseEntity<Usuarios> modificarUsuario(
            @PathVariable int id,
            @RequestBody Usuarios usuarioModificado,
            Authentication authentication) {
        return ResponseEntity.ok(
                usuariosService.modificarUsuario(id, usuarioModificado, authentication.getName()));
    }

    // Eliminar — borrado físico.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable int id,
            Authentication authentication) {
        usuariosService.eliminarUsuario(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // Desactivar — bloqueo de acceso (estado → false).
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarUsuario(
            @PathVariable int id,
            Authentication authentication) {
        usuariosService.desactivarUsuario(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // Activar — desbloqueo de acceso (estado → true).
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarUsuario(
            @PathVariable int id,
            Authentication authentication) {
        usuariosService.activarUsuario(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}