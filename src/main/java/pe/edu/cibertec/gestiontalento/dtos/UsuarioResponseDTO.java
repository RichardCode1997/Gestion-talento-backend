package pe.edu.cibertec.gestiontalento.dtos;

import lombok.Data;
import pe.edu.cibertec.gestiontalento.model.Usuarios;

/**
 * DTO de respuesta para usuarios.
 * - Filtra SUPERADMIN del listado (se aplica en UsuariosService).
 * - Aplana los datos del empleado vinculado a solo nombre, apellido y dni
 *   para evitar exponer el objeto Empleados completo.
 * - No expone la contraseña.
 */
@Data
public class UsuarioResponseDTO {

    private int     idUsuario;
    private String  correo;
    private boolean estado;
    private String  nombreRol;   // antes: usuario.rol.nombreRol
    private int     idRol;       // necesario para el combobox de edición en el frontend

    // Datos mínimos del empleado vinculado
    private int    idEmpleado;       // necesario para identificar al empleado
    private String nombreEmpleado;   // nombre + apellido del empleado
    private String dniEmpleado;      // para identificación rápida en la tabla

    /**
     * Construye el DTO a partir de la entidad Usuarios.
     * El empleado viene dentro del usuario (usuarios.empleado).
     */
    public static UsuarioResponseDTO from(Usuarios usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setCorreo(usuario.getCorreo());
        dto.setEstado(usuario.isEstado());

        if (usuario.getRol() != null) {
            dto.setNombreRol(usuario.getRol().getNombreRol());
            dto.setIdRol(usuario.getRol().getIdRol());
        }

        if (usuario.getEmpleado() != null) {
            dto.setIdEmpleado(usuario.getEmpleado().getIdEmpleado());
            dto.setNombreEmpleado(
                    usuario.getEmpleado().getNombre() + " " + usuario.getEmpleado().getApellido()
            );
            dto.setDniEmpleado(usuario.getEmpleado().getDni());
        }

        return dto;
    }
}