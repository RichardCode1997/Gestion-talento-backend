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

    public Usuarios crearUsuario(Usuarios usuario, String correoAutenticado) {
        // Obtener rol del usuario autenticado
        Usuarios usuarioAutenticado = obtenerPorCorreo(correoAutenticado);
        String rolAutenticado = usuarioAutenticado.getRol().getNombreRol();

        // Validación 1: duplicado por correo
        if (usuariosRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("Error: El correo '" + usuario.getCorreo() + "' ya está registrado en el sistema.");
        }

        // Validación 2: contraseña obligatoria
        if (usuario.getContraseña() == null || usuario.getContraseña().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        // Validación 3: buscar el rol completo
        Roles rolCompleto = rolesRepository.findById(usuario.getRol().getIdRol())
                .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe"));

        // Validación 4: nadie puede crear un SUPERADMIN
        if (rolCompleto.getNombreRol().equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("No se puede crear un usuario con rol SUPERADMIN.");
        }

        // Validación 5: solo SUPERADMIN puede crear un ADMINISTRADOR
        if (rolCompleto.getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Solo el SUPERADMIN puede crear un usuario con rol ADMINISTRADOR.");
        }

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

        // Obtener rol del usuario autenticado
        Usuarios usuarioAutenticado = obtenerPorCorreo(correoAutenticado);
        String rolAutenticado = usuarioAutenticado.getRol().getNombreRol();

        // Validación 1: no puedes cambiar tu propio rol
        if (usuarioExistente.getCorreo().equalsIgnoreCase(correoAutenticado) &&
                usuarioModificado.getRol() != null &&
                usuarioModificado.getRol().getIdRol() != usuarioExistente.getRol().getIdRol()) {
            throw new IllegalArgumentException("No puedes cambiar tu propio rol.");
        }

        // Validación 2: nadie puede modificar a un SUPERADMIN
        if (usuarioExistente.getRol().getNombreRol().equalsIgnoreCase("SUPERADMIN") &&
                !usuarioExistente.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new IllegalArgumentException("No se puede modificar a un usuario con rol SUPERADMIN.");
        }

        // Validación 3: solo SUPERADMIN puede modificar a un ADMINISTRADOR
        if (usuarioExistente.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !usuarioExistente.getCorreo().equalsIgnoreCase(correoAutenticado) &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Solo el SUPERADMIN puede modificar a un ADMINISTRADOR.");
        }

        // Validación 4 y 5: restricciones al asignar roles
        if (usuarioModificado.getRol() != null) {
            Roles rolNuevo = rolesRepository.findById(usuarioModificado.getRol().getIdRol())
                    .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe"));

            // Validación 4: nadie puede asignar rol SUPERADMIN
            if (rolNuevo.getNombreRol().equalsIgnoreCase("SUPERADMIN")) {
                throw new IllegalArgumentException("No puedes asignar el rol SUPERADMIN a ningún usuario.");
            }

            // Validación 5: solo SUPERADMIN puede asignar rol ADMINISTRADOR
            if (rolNuevo.getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                    !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
                throw new IllegalArgumentException("Solo el SUPERADMIN puede asignar el rol ADMINISTRADOR.");
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

        // Obtener rol del usuario autenticado
        Usuarios usuarioAutenticado = obtenerPorCorreo(correoAutenticado);
        String rolAutenticado = usuarioAutenticado.getRol().getNombreRol();

        // Validación 1: no puedes desactivarte a ti mismo
        if (usuario.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new IllegalArgumentException("No puedes desactivar tu propia cuenta.");
        }

        // Validación 2: nadie puede desactivar a un SUPERADMIN
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("No se puede desactivar a un usuario con rol SUPERADMIN.");
        }

        // Validación 3: solo SUPERADMIN puede desactivar a un ADMINISTRADOR
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Solo el SUPERADMIN puede desactivar a un ADMINISTRADOR.");
        }

        usuario.setEstado(false);
        usuariosRepository.save(usuario);
    }

    public void activarUsuario(int id, String correoAutenticado) {
        Usuarios usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener rol del usuario autenticado
        Usuarios usuarioAutenticado = obtenerPorCorreo(correoAutenticado);
        String rolAutenticado = usuarioAutenticado.getRol().getNombreRol();

        // Validación 1: nadie puede activar a un SUPERADMIN
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("No se puede activar a un usuario con rol SUPERADMIN.");
        }

        // Validación 2: solo SUPERADMIN puede activar a un ADMINISTRADOR
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Solo el SUPERADMIN puede activar a un ADMINISTRADOR.");
        }

        // Validación 3: no se puede activar el usuario de un empleado cesado
        Optional<Empleados> empleado = empleadosRepository.findByUsuarioIdUsuario(id);
        if (empleado.isPresent() && empleado.get().getEstado() == EstadoEmpleado.Cesado) {
            throw new IllegalArgumentException("No se puede activar el usuario de un empleado cesado.");
        }

        usuario.setEstado(true);
        usuariosRepository.save(usuario);
    }

    public void eliminarUsuario(int id, String correoAutenticado) {
        Usuarios usuario = obtenerUsuarioPorId(id);

        // Obtener rol del usuario autenticado
        Usuarios usuarioAutenticado = obtenerPorCorreo(correoAutenticado);
        String rolAutenticado = usuarioAutenticado.getRol().getNombreRol();

        // Validación 1: no puedes eliminarte a ti mismo
        if (usuario.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new IllegalArgumentException("No puedes eliminar tu propia cuenta.");
        }

        // Validación 2: nadie puede eliminar a un SUPERADMIN
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("No se puede eliminar a un usuario con rol SUPERADMIN.");
        }

        // Validación 3: solo SUPERADMIN puede eliminar a un ADMINISTRADOR
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Solo el SUPERADMIN puede eliminar a un ADMINISTRADOR.");
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