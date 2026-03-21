package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Empleados;
import pe.edu.cibertec.gestiontalento.repository.DepartamentosRepository;
import pe.edu.cibertec.gestiontalento.repository.EmpleadosRepository;
import pe.edu.cibertec.gestiontalento.repository.HorariosRepository;
import pe.edu.cibertec.gestiontalento.repository.UsuariosRepository;

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
        empleado.setEstado(true); // Aseguramos que nazca activo
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

    public List<Empleados> listarEmpleados() {
        return empleadosRepository.findAll();
    }

    public List<Empleados> listarSoloActivos() {
        return empleadosRepository.findByEstadoTrue();
    }

    public List<Empleados> listarSoloInactivos() {
        return empleadosRepository.findByEstadoFalse();
    }

    public Empleados obtenerPorDni(String dni) {
        return empleadosRepository.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún empleado con el DNI: " + dni));
    }

    public Empleados obtenerEmpleadoPorId(int id) {
        return empleadosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el empleado con ID: " + id));
    }

    public Empleados actualizarEmpleado(int id, Empleados datosNuevos) {
        // Buscamos al empleado que ya existe en la BD
        Empleados empleadoExistente = obtenerEmpleadoPorId(id);

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
        empleadoExistente.setUsuario(datosNuevos.getUsuario());
        empleadoExistente.setHorario(datosNuevos.getHorario());
        empleadoExistente.setDepartamento(datosNuevos.getDepartamento());

        cargarRelaciones(empleadoExistente);
        return empleadosRepository.save(empleadoExistente);
    }

    public void eliminarEmpleado(int id) {
        // 1. Buscamos al empleado y sus relaciones
        Empleados empleado = obtenerEmpleadoPorId(id);

        // 2. Guardamos la referencia del usuario antes de borrar nada
        // Si el usuario es NULL, no pasará nada, pero si existe, lo tenemos listo
        int idUsuarioAsociado = (empleado.getUsuario() != null)
                ? empleado.getUsuario().getIdUsuario()
                : -1;

        // 3. Borramos al EMPLEADO primero
        // Esto es vital porque el empleado es quien tiene la FK (llave foránea)
        empleadosRepository.delete(empleado);

        // 4. Si el empleado tenía un usuario, lo borramos ahora que ya no hay vínculos
        if (idUsuarioAsociado != -1) {
            usuariosRepository.deleteById(idUsuarioAsociado);
        }
    }

    public void desactivarEmpleado(int id) {
        Empleados empleado = obtenerEmpleadoPorId(id);

        // Apagamos al empleado
        empleado.setEstado(false);

        // Apagamos también su cuenta de usuario para que no pueda entrar
        if (empleado.getUsuario() != null) {
            empleado.getUsuario().setEstado(false);
        }

        empleadosRepository.save(empleado);
    }

    public void activarEmpleado(int id) {
        // 1. Buscamos al empleado (aunque esté inactivo, el ID sigue existiendo)
        Empleados empleado = obtenerEmpleadoPorId(id);

        // 2. Cambiamos su estado a true (vuelve a ser 1 en la BD)
        empleado.setEstado(true);

        // 3. También reactivamos su cuenta de usuario para que pueda volver a entrar
        if (empleado.getUsuario() != null) {
            empleado.getUsuario().setEstado(true);
        }

        // 4. Guardamos los cambios
        empleadosRepository.save(empleado);
    }
}