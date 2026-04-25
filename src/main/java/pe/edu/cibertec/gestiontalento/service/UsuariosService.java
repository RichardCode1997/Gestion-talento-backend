package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Roles;
import pe.edu.cibertec.gestiontalento.model.Usuarios;
import pe.edu.cibertec.gestiontalento.repository.RolesRepository;
import pe.edu.cibertec.gestiontalento.repository.UsuariosRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import pe.edu.cibertec.gestiontalento.model.Empleados;
import pe.edu.cibertec.gestiontalento.model.EstadoEmpleado;
import pe.edu.cibertec.gestiontalento.repository.EmpleadosRepository;
import java.util.Optional;

import java.util.List;

@Service
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;
    private final EmpleadosRepository empleadosRepository;

    @Autowired
    public UsuariosService(UsuariosRepository usuariosRepository,
                           PasswordEncoder passwordEncoder,
                           RolesRepository rolesRepository,
                           EmpleadosRepository empleadosRepository) {
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
        this.empleadosRepository = empleadosRepository;
    }

    public Usuarios crearUsuario(Usuarios usuario) {
        // 1. Validación de duplicado por correo
        if (usuariosRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("Error: El correo '" + usuario.getCorreo() + "' ya está registrado en el sistema.");
        }

        // 2. Validación de seguridad para evitar el error 500
        if (usuario.getContraseña() == null || usuario.getContraseña().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria en el JSON");
        }

        // 3. Buscamos el rol completo en la DB usando el ID que viene en el JSON
        Roles rolCompleto = rolesRepository.findById(usuario.getRol().getIdRol())
                .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe"));

        // 4. Se lo asignamos al usuario (ahora el objeto ya tiene el nombre del rol)
        usuario.setRol(rolCompleto);

        usuario.setEstado(true);

        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        return usuariosRepository.save(usuario);
    }

    public Page<Usuarios> listarUsuarios(Pageable pageable) {
        return usuariosRepository.findAll(pageable);
    }

    public List<Usuarios> listarUsuariosActivos() {
        return usuariosRepository.findByEstadoTrue();
    }

    public List<Usuarios> listarUsuariosInactivos() {
        return usuariosRepository.findByEstadoFalse();
    }

    public Usuarios obtenerPorCorreo(String correo) {
        return usuariosRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con el correo: " + correo));
    }

    public Usuarios obtenerUsuarioPorId(int id) {
        return usuariosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario con ID: " + id));
    }

    public Usuarios modificarUsuario(int id, Usuarios usuarioModificado, String correoAutenticado) {
        Usuarios usuarioExistente = obtenerUsuarioPorId(id);

        // Validación: no puedes cambiar tu propio rol
        if (usuarioExistente.getCorreo().equalsIgnoreCase(correoAutenticado) &&
                usuarioModificado.getRol() != null &&
                usuarioModificado.getRol().getIdRol() != usuarioExistente.getRol().getIdRol()) {
            throw new IllegalArgumentException("No puedes cambiar tu propio rol.");
        }
        // Validación: no puedes editar a otro ADMINISTRADOR
        if (usuarioExistente.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !usuarioExistente.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new IllegalArgumentException("No se puede modificar a un usuario con rol ADMINISTRADOR.");
        }

        // Validación: no puedes asignar rol ADMINISTRADOR a otro usuario
        if (usuarioModificado.getRol() != null) {
            Roles rolNuevo = rolesRepository.findById(usuarioModificado.getRol().getIdRol())
                    .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe"));
            if (rolNuevo.getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                    !usuarioExistente.getCorreo().equalsIgnoreCase(correoAutenticado)) {
                throw new IllegalArgumentException("No puedes asignar el rol ADMINISTRADOR a otro usuario.");
            }
        }

        // 1. Actualizar el correo
        // Solo validamos si el correo que envían es DIFERENTE al que ya tiene
        if (!usuarioExistente.getCorreo().equalsIgnoreCase(usuarioModificado.getCorreo())) {
            if (usuariosRepository.findByCorreo(usuarioModificado.getCorreo()).isPresent()) {
                throw new IllegalArgumentException("No se puede actualizar: El correo '" + usuarioModificado.getCorreo() + "' ya pertenece a otro usuario.");
            }
            usuarioExistente.setCorreo(usuarioModificado.getCorreo());
        }

        // 2. BUSCAR EL ROL COMPLETO (Para evitar el null en el JSON de respuesta)
        if (usuarioModificado.getRol() != null) {
            Roles rolCompleto = rolesRepository.findById(usuarioModificado.getRol().getIdRol())
                    .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe"));
            usuarioExistente.setRol(rolCompleto);
        }

        // 3. Actualizar contraseña solo si se envía una nueva
        if (usuarioModificado.getContraseña() != null && !usuarioModificado.getContraseña().isEmpty()) {
            usuarioExistente.setContraseña(passwordEncoder.encode(usuarioModificado.getContraseña()));
        }

        return usuariosRepository.save(usuarioExistente);
    }

    public void desactivarUsuario(int id, String correoAutenticado) {
        Usuarios usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // No puedes desactivarte a ti mismo
        if (usuario.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new IllegalArgumentException("No puedes desactivar tu propia cuenta.");
        }

        // No puedes desactivar a un ADMINISTRADOR
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR")) {
            throw new IllegalArgumentException("No se puede desactivar a un usuario con rol ADMINISTRADOR.");
        }

        usuario.setEstado(false);
        usuariosRepository.save(usuario);
    }

    public void activarUsuario(int id) {
        Usuarios usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Bloquear si el empleado vinculado está CESADO
        Optional<Empleados> empleado = empleadosRepository.findByUsuarioIdUsuario(id);
        if (empleado.isPresent() && empleado.get().getEstado() == EstadoEmpleado.Cesado) {
            throw new IllegalArgumentException(
                    "No se puede activar el usuario de un empleado cesado."
            );
        }

        usuario.setEstado(true);
        usuariosRepository.save(usuario);
    }

    public void eliminarUsuario(int id, String correoAutenticado) {
        Usuarios usuario = obtenerUsuarioPorId(id);

        // No puedes eliminarte a ti mismo
        if (usuario.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new IllegalArgumentException("No puedes eliminar tu propia cuenta.");
        }

        // No puedes eliminar a un ADMINISTRADOR
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR")) {
            throw new IllegalArgumentException("No se puede eliminar a un usuario con rol ADMINISTRADOR.");
        }

        // Desvincular empleado antes de borrar el usuario
        Optional<Empleados> empleado = empleadosRepository.findByUsuarioIdUsuario(id);
        if (empleado.isPresent()) {
            empleado.get().setUsuario(null);
            empleadosRepository.save(empleado.get());
        }

        usuariosRepository.delete(usuario);
    }
}