package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.gestiontalento.model.Empleados;
import pe.edu.cibertec.gestiontalento.model.EstadoEmpleado;
import java.util.Optional;
import java.util.List;

@Repository
public interface EmpleadosRepository extends JpaRepository<Empleados, Integer> {

    // Crítico: Para buscar a un empleado por su documento de identidad
    Optional<Empleados> findByDni(String dni);

    // Para búsquedas rápidas en el buscador del sistema
    List<Empleados> findByApellidoContainingIgnoreCase(String apellido);

    // Para encontrar al empleado vinculado a una cuenta de usuario específica
    Optional<Empleados> findByUsuarioIdUsuario(int idUsuario);

    //
    Optional<Empleados> findByCelular(String celular);

    // Para listar a los empleados
    List<Empleados> findByEstado(EstadoEmpleado estado);

    List<Empleados> findByUsuarioIsNull();

}