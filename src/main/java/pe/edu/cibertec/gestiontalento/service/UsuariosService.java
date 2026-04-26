package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.dtos.UsuarioResponseDTO;
import pe.edu.cibertec.gestiontalento.model.Empleados;
import pe.edu.cibertec.gestiontalento.model.EstadoEmpleado;
import pe.edu.cibertec.gestiontalento.model.Roles;
import pe.edu.cibertec.gestiontalento.model.Usuarios;
import pe.edu.cibertec.gestiontalento.repository.EmpleadosRepository;
import pe.edu.cibertec.gestiontalento.repository.RolesRepository;
import pe.edu.cibertec.gestiontalento.repository.UsuariosRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuariosService {

    private final UsuariosRepository  usuariosRepository;
    private final PasswordEncoder     passwordEncoder;
    private final RolesRepository     rolesRepository;
    private final EmpleadosRepository empleadosRepository;

    @Autowired
    public UsuariosService(UsuariosRepository usuariosRepository,
                           PasswordEncoder passwordEncoder,
                           RolesRepository rolesRepository,
                           EmpleadosRepository empleadosRepository) {
        this.usuariosRepository  = usuariosRepository;
        this.passwordEncoder     = passwordEncoder;
        this.rolesRepository     = rolesRepository;
        this.empleadosRepository = empleadosRepository;
    }

    // -----------------------------------------------------------------------
    // Helper: convierte Usuarios → UsuarioResponseDTO.
    // -----------------------------------------------------------------------
    private UsuarioResponseDTO toDTO(Usuarios usuario) {
        return UsuarioResponseDTO.from(usuario);
    }

    // -----------------------------------------------------------------------
    // LISTAR — devuelve DTOs filtrando SUPERADMIN en el backend.
    // -----------------------------------------------------------------------
    public Page<UsuarioResponseDTO> listarUsuarios(Pageable pageable) {
        Page<Usuarios> pagina = usuariosRepository.findAll(pageable);

        List<UsuarioResponseDTO> dtos = pagina.getContent().stream()
                .map(this::toDTO)
                .filter(dto -> !"SUPERADMIN".equals(dto.getNombreRol()))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, pagina.getTotalElements());
    }

    public List<UsuarioResponseDTO> listarUsuariosActivos() {
        return usuariosRepository.findByEstadoTrue().stream()
                .map(this::toDTO)
                .filter(dto -> !"SUPERADMIN".equals(dto.getNombreRol()))
                .collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> listarUsuariosInactivos() {
        return usuariosRepository.findByEstadoFalse().stream()
                .map(this::toDTO)
                .filter(dto -> !"SUPERADMIN".equals(dto.getNombreRol()))
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------------------
    // Los métodos que operan sobre un usuario individual siguen devolviendo
    // la entidad directamente — no necesitan DTO.
    // -----------------------------------------------------------------------

    public Usuarios obtenerPorCorreo(String correo) {
        return usuariosRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un usuario con el correo: " + correo));
    }

    public Usuarios obtenerUsuarioPorId(int id) {
        return usuariosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el usuario con ID: " + id));
    }

    public Usuarios crearUsuario(Usuarios usuario, String correoAutenticado) {

        Usuarios usuarioAutenticado = obtenerPorCorreo(correoAutenticado);
        String rolAutenticado = usuarioAutenticado.getRol().getNombreRol();

        // Validación 1: el empleado vinculado es OBLIGATORIO
        if (usuario.getEmpleado() == null) {
            throw new IllegalArgumentException("Debe vincular un empleado al crear un usuario.");
        }

        // Validación 2: el empleado debe existir en la BD
        Empleados empleadoCompleto = empleadosRepository.findById(usuario.getEmpleado().getIdEmpleado())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un empleado con ID: " + usuario.getEmpleado().getIdEmpleado()));

        // Validación 3: el empleado no puede estar cesado
        if (empleadoCompleto.getEstado() == EstadoEmpleado.Cesado) {
            throw new IllegalArgumentException("No se puede crear un usuario para un empleado cesado.");
        }

        // Validación 4: el empleado no debe estar ya vinculado a otro usuario (1:1)
        if (usuariosRepository.findByEmpleadoIdEmpleado(empleadoCompleto.getIdEmpleado()).isPresent()) {
            throw new IllegalArgumentException(
                    "El empleado '" + empleadoCompleto.getNombre() + " " + empleadoCompleto.getApellido()
                            + "' ya tiene un usuario asignado.");
        }

        // Validación 5: correo duplicado
        if (usuariosRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalArgumentException(
                    "El correo '" + usuario.getCorreo() + "' ya está registrado en el sistema.");
        }

        // Validación 6: contraseña obligatoria
        if (usuario.getContraseña() == null || usuario.getContraseña().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        // Validación 7: buscar el rol completo
        Roles rolCompleto = rolesRepository.findById(usuario.getRol().getIdRol())
                .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe."));

        // Validación 8: nadie puede crear un SUPERADMIN
        if (rolCompleto.getNombreRol().equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("No se puede crear un usuario con rol SUPERADMIN.");
        }

        // Validación 9: solo SUPERADMIN puede crear un ADMINISTRADOR
        if (rolCompleto.getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Solo el SUPERADMIN puede crear un usuario con rol ADMINISTRADOR.");
        }

        usuario.setRol(rolCompleto);
        usuario.setEmpleado(empleadoCompleto);
        usuario.setEstado(true);
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));

        return usuariosRepository.save(usuario);
    }

    public Usuarios modificarUsuario(int id, Usuarios usuarioModificado, String correoAutenticado) {

        Usuarios usuarioExistente = obtenerUsuarioPorId(id);
        Usuarios usuarioAutenticado = obtenerPorCorreo(correoAutenticado);
        String rolAutenticado = usuarioAutenticado.getRol().getNombreRol();

        // Validación 1: no puedes cambiar tu propio rol
        if (usuarioExistente.getCorreo() != null &&
                usuarioExistente.getCorreo().equalsIgnoreCase(correoAutenticado) &&
                usuarioModificado.getRol() != null &&
                usuarioModificado.getRol().getIdRol() != usuarioExistente.getRol().getIdRol()) {
            throw new IllegalArgumentException("No puedes cambiar tu propio rol.");
        }

        // Validación 2: nadie puede modificar a un SUPERADMIN (salvo él mismo)
        if (usuarioExistente.getRol().getNombreRol().equalsIgnoreCase("SUPERADMIN") &&
                (usuarioExistente.getCorreo() == null ||
                        !usuarioExistente.getCorreo().equalsIgnoreCase(correoAutenticado))) {
            throw new IllegalArgumentException("No se puede modificar a un usuario con rol SUPERADMIN.");
        }

        // Validación 3: solo SUPERADMIN puede modificar a un ADMINISTRADOR
        if (usuarioExistente.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                (usuarioExistente.getCorreo() == null ||
                        !usuarioExistente.getCorreo().equalsIgnoreCase(correoAutenticado)) &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Solo el SUPERADMIN puede modificar a un ADMINISTRADOR.");
        }

        // Validación 4 y 5: restricciones al asignar roles
        if (usuarioModificado.getRol() != null) {
            Roles rolNuevo = rolesRepository.findById(usuarioModificado.getRol().getIdRol())
                    .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe."));

            if (rolNuevo.getNombreRol().equalsIgnoreCase("SUPERADMIN")) {
                throw new IllegalArgumentException("No puedes asignar el rol SUPERADMIN a ningún usuario.");
            }
            if (rolNuevo.getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                    !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
                throw new IllegalArgumentException("Solo el SUPERADMIN puede asignar el rol ADMINISTRADOR.");
            }
        }

        // Actualizar correo — null-safe
        if (usuarioModificado.getCorreo() != null) {
            if (!usuarioModificado.getCorreo().equalsIgnoreCase(usuarioExistente.getCorreo())) {
                if (usuariosRepository.findByCorreo(usuarioModificado.getCorreo()).isPresent()) {
                    throw new IllegalArgumentException(
                            "No se puede actualizar: el correo '" + usuarioModificado.getCorreo()
                                    + "' ya pertenece a otro usuario.");
                }
                usuarioExistente.setCorreo(usuarioModificado.getCorreo());
            }
        }

        // Actualizar rol
        if (usuarioModificado.getRol() != null) {
            Roles rolCompleto = rolesRepository.findById(usuarioModificado.getRol().getIdRol())
                    .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe."));
            usuarioExistente.setRol(rolCompleto);
        }

        // Actualizar contraseña solo si se envía una nueva
        if (usuarioModificado.getContraseña() != null && !usuarioModificado.getContraseña().isEmpty()) {
            usuarioExistente.setContraseña(passwordEncoder.encode(usuarioModificado.getContraseña()));
        }

        // La vinculación empleado-usuario es PERMANENTE — no se permite cambiar el empleado.

        return usuariosRepository.save(usuarioExistente);
    }

    public void desactivarUsuario(int id, String correoAutenticado) {
        Usuarios usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Usuarios usuarioAutenticado = obtenerPorCorreo(correoAutenticado);
        String rolAutenticado = usuarioAutenticado.getRol().getNombreRol();

        if (usuario.getCorreo() != null && usuario.getCorreo().equalsIgnoreCase(correoAutenticado))
            throw new IllegalArgumentException("No puedes desactivar tu propia cuenta.");
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("SUPERADMIN"))
            throw new IllegalArgumentException("No se puede desactivar a un usuario con rol SUPERADMIN.");
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN"))
            throw new IllegalArgumentException("Solo el SUPERADMIN puede desactivar a un ADMINISTRADOR.");

        usuario.setEstado(false);
        usuariosRepository.save(usuario);
    }

    public void activarUsuario(int id, String correoAutenticado) {
        Usuarios usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Usuarios usuarioAutenticado = obtenerPorCorreo(correoAutenticado);
        String rolAutenticado = usuarioAutenticado.getRol().getNombreRol();

        if (usuario.getRol().getNombreRol().equalsIgnoreCase("SUPERADMIN"))
            throw new IllegalArgumentException("No se puede activar a un usuario con rol SUPERADMIN.");
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN"))
            throw new IllegalArgumentException("Solo el SUPERADMIN puede activar a un ADMINISTRADOR.");

        // No se puede activar el usuario si su empleado está cesado
        if (usuario.getEmpleado() != null &&
                usuario.getEmpleado().getEstado() == EstadoEmpleado.Cesado)
            throw new IllegalArgumentException("No se puede activar el usuario de un empleado cesado.");

        usuario.setEstado(true);
        usuariosRepository.save(usuario);
    }

    public void eliminarUsuario(int id, String correoAutenticado) {
        Usuarios usuario = obtenerUsuarioPorId(id);
        Usuarios usuarioAutenticado = obtenerPorCorreo(correoAutenticado);
        String rolAutenticado = usuarioAutenticado.getRol().getNombreRol();

        if (usuario.getCorreo() != null && usuario.getCorreo().equalsIgnoreCase(correoAutenticado))
            throw new IllegalArgumentException("No puedes eliminar tu propia cuenta.");
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("SUPERADMIN"))
            throw new IllegalArgumentException("No se puede eliminar a un usuario con rol SUPERADMIN.");
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN"))
            throw new IllegalArgumentException("Solo el SUPERADMIN puede eliminar a un ADMINISTRADOR.");

        usuariosRepository.delete(usuario);
    }
}