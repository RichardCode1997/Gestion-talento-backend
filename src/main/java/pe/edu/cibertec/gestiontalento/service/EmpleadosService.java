package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.dtos.EmpleadoResponseDTO;
import pe.edu.cibertec.gestiontalento.model.Empleados;
import pe.edu.cibertec.gestiontalento.model.EstadoEmpleado;
import pe.edu.cibertec.gestiontalento.model.Usuarios;
import pe.edu.cibertec.gestiontalento.repository.DepartamentosRepository;
import pe.edu.cibertec.gestiontalento.repository.EmpleadosRepository;
import pe.edu.cibertec.gestiontalento.repository.HorariosRepository;
import pe.edu.cibertec.gestiontalento.repository.UsuariosRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpleadosService {

    private final EmpleadosRepository empleadosRepository;
    private final UsuariosRepository usuariosRepository;
    private final HorariosRepository horariosRepository;
    private final DepartamentosRepository departamentosRepository;

    @Autowired
    public EmpleadosService(EmpleadosRepository empleadosRepository,
                            UsuariosRepository usuariosRepository,
                            HorariosRepository horariosRepository,
                            DepartamentosRepository departamentosRepository) {
        this.empleadosRepository = empleadosRepository;
        this.usuariosRepository = usuariosRepository;
        this.horariosRepository = horariosRepository;
        this.departamentosRepository = departamentosRepository;
    }


    // -----------------------------------------------------------------------
    // Helper: carga relaciones horario y departamento desde BD.
    // -----------------------------------------------------------------------
    private void cargarRelaciones(Empleados empleado) {
        if (empleado.getHorario() != null) {
            horariosRepository.findById(empleado.getHorario().getIdHorario())
                    .ifPresent(empleado::setHorario);
        }
        if (empleado.getDepartamento() != null) {
            departamentosRepository.findById(empleado.getDepartamento().getIdDepartamento())
                    .ifPresent(empleado::setDepartamento);
        }
    }

    // -----------------------------------------------------------------------
    // Helper: obtiene el usuario vinculado a un empleado.
    // -----------------------------------------------------------------------
    private Optional<Usuarios> obtenerUsuarioDeEmpleado(int idEmpleado) {
        return usuariosRepository.findByEmpleadoIdEmpleado(idEmpleado);
    }

    // -----------------------------------------------------------------------
    // Helper: obtiene el rol del empleado vía su usuario vinculado.
    // -----------------------------------------------------------------------
    private String obtenerRolDeEmpleado(Empleados empleado) {
        return obtenerUsuarioDeEmpleado(empleado.getIdEmpleado())
                .map(u -> u.getRol().getNombreRol())
                .orElse("");
    }

    // -----------------------------------------------------------------------
    // Helper: convierte Empleados → EmpleadoResponseDTO con datos del usuario.
    // -----------------------------------------------------------------------
    private EmpleadoResponseDTO toDTO(Empleados emp) {
        Usuarios usuario = obtenerUsuarioDeEmpleado(emp.getIdEmpleado()).orElse(null);
        return EmpleadoResponseDTO.from(emp, usuario);  // ← faltaba este return y cerrar llave
    }

    // -----------------------------------------------------------------------
    // LISTAR — devuelve DTOs filtrando SUPERADMIN en el backend.
    // -----------------------------------------------------------------------
    public Page<EmpleadoResponseDTO> listarEmpleados(Pageable pageable) {
        Page<Empleados> pagina = empleadosRepository.findAll(pageable);

        List<EmpleadoResponseDTO> dtos = pagina.getContent().stream()
                .map(this::toDTO)
                .filter(dto -> !"SUPERADMIN".equals(dto.getRolUsuario()))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, pagina.getTotalElements());
    }


    public List<EmpleadoResponseDTO> listarPorEstado(EstadoEmpleado estado) {
        return empleadosRepository.findByEstado(estado).stream()
                .map(this::toDTO)
                .filter(dto -> !"SUPERADMIN".equals(dto.getRolUsuario()))
                .collect(Collectors.toList());
    }

    public EmpleadoResponseDTO obtenerEmpleadoPorIdDTO(int id) {
        return toDTO(obtenerEmpleadoPorId(id));
    }

    public Empleados obtenerPorDni(String dni) {
        return empleadosRepository.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró ningún empleado con el DNI: " + dni));
    }

    public Empleados obtenerEmpleadoPorId(int id) {
        return empleadosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el empleado con ID: " + id));
    }

    // Lista empleados que aún no tienen usuario vinculado.
    // Usado por el combobox del formulario de creación de usuarios.
    public List<Empleados> listarSinUsuario() {
        return empleadosRepository.findEmpleadosSinUsuario();
    }

    public Empleados crearEmpleado(Empleados empleado, String correoAutenticado) {

        String rolAutenticado = usuariosRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"))
                .getRol().getNombreRol();

        // Validación 1: DNI único
        if (empleadosRepository.findByDni(empleado.getDni()).isPresent()) {
            throw new IllegalArgumentException("El DNI " + empleado.getDni() + " ya está registrado.");
        }

        // Validación 2: celular único
        if (empleadosRepository.findByCelular(empleado.getCelular()).isPresent()) {
            throw new IllegalArgumentException("El número de celular " + empleado.getCelular() + " ya está registrado.");
        }

        cargarRelaciones(empleado);
        empleado.setEstado(EstadoEmpleado.Activo);

        return empleadosRepository.save(empleado);
    }

    public Empleados actualizarEmpleado(int id, Empleados datosNuevos, String correoAutenticado) {

        Empleados empleadoExistente = obtenerEmpleadoPorId(id);

        String rolAutenticado = usuariosRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"))
                .getRol().getNombreRol();

        String rolDelEmpleado = obtenerRolDeEmpleado(empleadoExistente);

        // Validación 1: nadie puede editar a un empleado con rol SUPERADMIN
        if (rolDelEmpleado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("No se puede editar a un empleado con rol SUPERADMIN.");
        }

        // Validación 2: solo SUPERADMIN puede editar a un empleado con rol ADMINISTRADOR
        if (rolDelEmpleado.equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Solo el SUPERADMIN puede editar a un empleado con rol ADMINISTRADOR.");
        }

        // Validación: campos obligatorios
        if (datosNuevos.getNombre() == null || datosNuevos.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (datosNuevos.getApellido() == null || datosNuevos.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        if (datosNuevos.getDni() == null || datosNuevos.getDni().trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }
        if (datosNuevos.getCelular() == null || datosNuevos.getCelular().trim().isEmpty()) {
            throw new IllegalArgumentException("El celular es obligatorio.");
        }
        if (datosNuevos.getDireccion() == null || datosNuevos.getDireccion().trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria.");
        }
        if (datosNuevos.getDepartamento() == null) {
            throw new IllegalArgumentException("El departamento es obligatorio.");
        }
        if (datosNuevos.getHorario() == null) {
            throw new IllegalArgumentException("El horario es obligatorio.");
        }

        // Validación DNI único (solo si cambió)
        if (!empleadoExistente.getDni().equals(datosNuevos.getDni())) {
            if (empleadosRepository.findByDni(datosNuevos.getDni()).isPresent()) {
                throw new IllegalArgumentException("Error: El DNI '" + datosNuevos.getDni() + "' ya está en uso.");
            }
            empleadoExistente.setDni(datosNuevos.getDni());
        }

        // Validación celular único (solo si cambió)
        if (!empleadoExistente.getCelular().equals(datosNuevos.getCelular())) {
            if (empleadosRepository.findByCelular(datosNuevos.getCelular()).isPresent()) {
                throw new IllegalArgumentException("Error: El celular '" + datosNuevos.getCelular() + "' ya está en uso.");
            }
            empleadoExistente.setCelular(datosNuevos.getCelular());
        }

        empleadoExistente.setNombre(datosNuevos.getNombre());
        empleadoExistente.setApellido(datosNuevos.getApellido());
        empleadoExistente.setDireccion(datosNuevos.getDireccion());
        empleadoExistente.setHorario(datosNuevos.getHorario());
        empleadoExistente.setDepartamento(datosNuevos.getDepartamento());

        // NOTA: la vinculación con usuario NO se modifica desde aquí.
        // El usuario está en usuarios.id_empleado, no en empleados.

        cargarRelaciones(empleadoExistente);

        return empleadosRepository.save(empleadoExistente);
    }

    public void eliminarEmpleado(int id, String correoAutenticado) {

        Empleados empleado = obtenerEmpleadoPorId(id);

        String rolAutenticado = usuariosRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"))
                .getRol().getNombreRol();

        String rolDelEmpleado = obtenerRolDeEmpleado(empleado);

        // Validación 1: no puedes eliminarte a ti mismo
        Optional<Usuarios> usuarioDelEmpleado = obtenerUsuarioDeEmpleado(empleado.getIdEmpleado());
        if (usuarioDelEmpleado.isPresent() &&
                usuarioDelEmpleado.get().getCorreo() != null &&
                usuarioDelEmpleado.get().getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new IllegalArgumentException("No puedes eliminar tu propio empleado.");
        }

        // Validación 2: nadie puede eliminar a un empleado con rol SUPERADMIN
        if (rolDelEmpleado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("No se puede eliminar a un empleado con rol SUPERADMIN.");
        }

        // Validación 3: solo SUPERADMIN puede eliminar a un empleado con rol ADMINISTRADOR
        if (rolDelEmpleado.equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Solo el SUPERADMIN puede eliminar a un empleado con rol ADMINISTRADOR.");
        }

        // Con el nuevo modelo: la FK está en usuarios.id_empleado.
        // Primero borramos el usuario vinculado (si existe), luego el empleado.
        // Esto respeta la integridad referencial: no se puede borrar el empleado
        // mientras exista un usuario apuntando a él.
        usuarioDelEmpleado.ifPresent(usuariosRepository::delete);

        empleadosRepository.delete(empleado);
    }

    public void cambiarEstadoEmpleado(int id, EstadoEmpleado nuevoEstado, String correoLogueado) {

        Empleados empleado = obtenerEmpleadoPorId(id);

        String rolAutenticado = usuariosRepository.findByCorreo(correoLogueado)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"))
                .getRol().getNombreRol();

        String rolDelEmpleado = obtenerRolDeEmpleado(empleado);
        Optional<Usuarios> usuarioDelEmpleado = obtenerUsuarioDeEmpleado(empleado.getIdEmpleado());

        // Validación 1: nadie puede cambiarse a sí mismo
        if (usuarioDelEmpleado.isPresent() &&
                usuarioDelEmpleado.get().getCorreo() != null &&
                usuarioDelEmpleado.get().getCorreo().equalsIgnoreCase(correoLogueado)) {
            throw new IllegalArgumentException("No puedes cambiar tu propio estado.");
        }

        // Validación 2: nadie puede modificar el estado de un empleado con rol SUPERADMIN
        if (rolDelEmpleado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("No se puede modificar el estado de un empleado con rol SUPERADMIN.");
        }

        // Validación 3: solo SUPERADMIN puede modificar el estado de un empleado con rol ADMINISTRADOR
        if (rolDelEmpleado.equalsIgnoreCase("ADMINISTRADOR") &&
                !rolAutenticado.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Solo el SUPERADMIN puede modificar el estado de un empleado con rol ADMINISTRADOR.");
        }

        empleado.setEstado(nuevoEstado);

        // Regla de negocio: si el empleado es CESADO, su usuario se desactiva automáticamente
        if (nuevoEstado == EstadoEmpleado.Cesado) {
            usuarioDelEmpleado.ifPresent(u -> {
                u.setEstado(false);
                usuariosRepository.save(u);
            });
        }

        empleadosRepository.save(empleado);
    }
}