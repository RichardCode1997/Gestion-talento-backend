package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Empleados;
import pe.edu.cibertec.gestiontalento.model.EstadoEmpleado;
import pe.edu.cibertec.gestiontalento.repository.DepartamentosRepository;
import pe.edu.cibertec.gestiontalento.repository.EmpleadosRepository;
import pe.edu.cibertec.gestiontalento.repository.HorariosRepository;
import pe.edu.cibertec.gestiontalento.repository.UsuariosRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    public Empleados crearEmpleado(Empleados empleado) {
        // 1. Validar DNI único
        if (empleadosRepository.findByDni(empleado.getDni()).isPresent()) {
            throw new IllegalArgumentException("El DNI " + empleado.getDni() + " ya está registrado.");
        }
        // 2. Validar Teléfono único
        if (empleadosRepository.findByCelular(empleado.getCelular()).isPresent()) {
            throw new IllegalArgumentException("El número de celular " + empleado.getCelular() + " ya está registrado.");
        }
        // 3. Validar Usuario Único
        if (empleado.getUsuario() != null) {
            int idUser = empleado.getUsuario().getIdUsuario();
            if (empleadosRepository.findByUsuarioIdUsuario(idUser).isPresent()) {
                throw new IllegalArgumentException("Error: El usuario con ID " + idUser + " ya está asignado a otro empleado.");
            }
        }

        cargarRelaciones(empleado);
        empleado.setEstado(EstadoEmpleado.Activo);// Aseguramos que nazca activo
        return empleadosRepository.save(empleado);
    }

    // Metodo privado para evitar repetir código y limpiar los NULLs en la respuesta
    private void cargarRelaciones(Empleados empleado) {
        if (empleado.getUsuario() != null) {
            usuariosRepository.findById(empleado.getUsuario().getIdUsuario())
                    .ifPresent(empleado::setUsuario);
        }
        if (empleado.getHorario() != null) {
            horariosRepository.findById(empleado.getHorario().getIdHorario())
                    .ifPresent(empleado::setHorario);
        }
        if (empleado.getDepartamento() != null) {
            departamentosRepository.findById(empleado.getDepartamento().getIdDepartamento())
                    .ifPresent(empleado::setDepartamento);
        }
    }

    public Page<Empleados> listarEmpleados(Pageable pageable) {
        return empleadosRepository.findAll(pageable);
    }

    public List<Empleados> listarPorEstado(EstadoEmpleado estado) {
        return empleadosRepository.findByEstado(estado);
    }

    public Empleados obtenerPorDni(String dni) {
        return empleadosRepository.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún empleado con el DNI: " + dni));
    }

    public Empleados obtenerEmpleadoPorId(int id) {
        return empleadosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el empleado con ID: " + id));
    }

    public List<Empleados> listarSinUsuario() {
        return empleadosRepository.findByUsuarioIsNull();
    }

    public Empleados actualizarEmpleado(int id, Empleados datosNuevos) {
        // Buscamos al empleado que ya existe en la BD
        Empleados empleadoExistente = obtenerEmpleadoPorId(id);

        // Validación: campos obligatorios no pueden ser NULL o vacíos
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

        if (datosNuevos.getUsuario() != null) {
            int idNuevoUser = datosNuevos.getUsuario().getIdUsuario();
            int idViejoUser = (empleadoExistente.getUsuario() != null)
                    ? empleadoExistente.getUsuario().getIdUsuario() : -1;

            // Solo validamos si está intentando cambiar de usuario
            if (idNuevoUser != idViejoUser) {
                if (empleadosRepository.findByUsuarioIdUsuario(idNuevoUser).isPresent()) {
                    throw new IllegalArgumentException("Error: El nuevo usuario ya está vinculado a otro empleado.");
                }
            }
        }

        // VALIDACIÓN DE DNI
        if (!empleadoExistente.getDni().equals(datosNuevos.getDni())) {
            if (empleadosRepository.findByDni(datosNuevos.getDni()).isPresent()) {
                throw new IllegalArgumentException("Error: El DNI '" + datosNuevos.getDni() + "' ya esta en uso.");
            }
            empleadoExistente.setDni(datosNuevos.getDni());
        }

        // VALIDACIÓN DE CELULAR
        if (!empleadoExistente.getCelular().equals(datosNuevos.getCelular())) {
            if (empleadosRepository.findByCelular(datosNuevos.getCelular()).isPresent()) {
                throw new IllegalArgumentException("Error: El celular '" + datosNuevos.getCelular() + "' ya esta en uso.");
            }
            empleadoExistente.setCelular(datosNuevos.getCelular());
        }

        // Mapeamos los campos básicos
        empleadoExistente.setNombre(datosNuevos.getNombre());
        empleadoExistente.setApellido(datosNuevos.getApellido());
        empleadoExistente.setDireccion(datosNuevos.getDireccion());

        // Mapeamos y cargamos las relaciones nuevas
        if (datosNuevos.getUsuario() != null) {
            empleadoExistente.setUsuario(datosNuevos.getUsuario());
        }
        empleadoExistente.setHorario(datosNuevos.getHorario());
        empleadoExistente.setDepartamento(datosNuevos.getDepartamento());

        cargarRelaciones(empleadoExistente);
        return empleadosRepository.save(empleadoExistente);
    }

    public void eliminarEmpleado(int id, String correoAutenticado) {
        // 1. Buscamos al empleado y sus relaciones
        Empleados empleado = obtenerEmpleadoPorId(id);

        // 2. Validaciones de seguridad
        // No puedes eliminarte a ti mismo
        if (empleado.getUsuario() != null &&
                empleado.getUsuario().getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new IllegalArgumentException("No puedes eliminar tu propio empleado.");
        }

        // No puedes eliminar a un empleado con rol ADMINISTRADOR
        if (empleado.getUsuario() != null &&
                empleado.getUsuario().getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR")) {
            throw new IllegalArgumentException("No se puede eliminar a un empleado con rol ADMINISTRADOR.");
        }

        // 3. Guardamos la referencia del usuario antes de borrar nada
        // Si el usuario es NULL, no pasará nada, pero si existe, lo tenemos listo
        int idUsuarioAsociado = (empleado.getUsuario() != null)
                ? empleado.getUsuario().getIdUsuario()
                : -1;

        // 4. Borramos al EMPLEADO primero
        // Esto es vital porque el empleado es quien tiene la FK (llave foránea)
        empleadosRepository.delete(empleado);

        // 5. Si el empleado tenía un usuario, lo borramos ahora que ya no hay vínculos
        if (idUsuarioAsociado != -1) {
            usuariosRepository.deleteById(idUsuarioAsociado);
        }
    }

    public void cambiarEstadoEmpleado(int id, EstadoEmpleado nuevoEstado, String correoLogueado) {
        Empleados empleado = obtenerEmpleadoPorId(id);

        // Regla: nadie puede cambiarse a sí mismo
        if (empleado.getUsuario() != null &&
                empleado.getUsuario().getCorreo().equalsIgnoreCase(correoLogueado)) {
            throw new IllegalArgumentException("No puedes cambiar tu propio estado.");
        }

        // Regla: no se puede modificar el estado de un empleado con rol ADMINISTRADOR
        if (empleado.getUsuario() != null &&
                empleado.getUsuario().getRol().getNombreRol().equalsIgnoreCase("ADMINISTRADOR")) {
            throw new IllegalArgumentException("No se puede modificar el estado de un empleado con rol ADMINISTRADOR.");
        }

        empleado.setEstado(nuevoEstado);

        // Regla de negocio: solo CESADO afecta al usuario automáticamente
        if (empleado.getUsuario() != null && nuevoEstado == EstadoEmpleado.Cesado) {
            empleado.getUsuario().setEstado(false);
        }

        empleadosRepository.save(empleado);
    }
}