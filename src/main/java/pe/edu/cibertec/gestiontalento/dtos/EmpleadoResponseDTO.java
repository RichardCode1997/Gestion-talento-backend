package pe.edu.cibertec.gestiontalento.dtos;

import lombok.Data;
import pe.edu.cibertec.gestiontalento.model.Departamentos;
import pe.edu.cibertec.gestiontalento.model.Empleados;
import pe.edu.cibertec.gestiontalento.model.EstadoEmpleado;
import pe.edu.cibertec.gestiontalento.model.Horarios;
import pe.edu.cibertec.gestiontalento.model.Usuarios;

/**
 * DTO de respuesta para empleados.
 * Aplana los datos del usuario vinculado (correo, rol) para que el frontend
 * pueda aplicar la lógica de permisos sin necesidad de navegar objetos anidados
 * que ya no existen en la entidad Empleados tras el rediseño de BD.
 *
 * El frontend accede a emp.correoUsuario y emp.rolUsuario directamente.
 */
@Data
public class EmpleadoResponseDTO {

    private int          idEmpleado;
    private String       nombre;
    private String       apellido;
    private String       dni;
    private String       celular;
    private String       direccion;
    private EstadoEmpleado estado;
    private Horarios     horario;
    private Departamentos departamento;

    // Datos del usuario vinculado — null si el empleado no tiene usuario
    private String correoUsuario;  // antes: emp.usuario?.correo
    private String rolUsuario;     // antes: emp.usuario?.rol?.nombreRol

    /**
     * Construye el DTO a partir de la entidad Empleados y su usuario vinculado.
     * @param emp     entidad empleado
     * @param usuario usuario vinculado (puede ser null si el empleado no tiene cuenta)
     */
    public static EmpleadoResponseDTO from(Empleados emp, Usuarios usuario) {
        EmpleadoResponseDTO dto = new EmpleadoResponseDTO();
        dto.setIdEmpleado(emp.getIdEmpleado());
        dto.setNombre(emp.getNombre());
        dto.setApellido(emp.getApellido());
        dto.setDni(emp.getDni());
        dto.setCelular(emp.getCelular());
        dto.setDireccion(emp.getDireccion());
        dto.setEstado(emp.getEstado());
        dto.setHorario(emp.getHorario());
        dto.setDepartamento(emp.getDepartamento());

        if (usuario != null) {
            dto.setCorreoUsuario(usuario.getCorreo());
            dto.setRolUsuario(usuario.getRol() != null ? usuario.getRol().getNombreRol() : null);
        }

        return dto;
    }
}