package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.gestiontalento.model.Empleados;
import pe.edu.cibertec.gestiontalento.model.EstadoEmpleado;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

@Repository
public interface EmpleadosRepository extends JpaRepository<Empleados, Integer> {

    // Crítico: Para buscar a un empleado por su documento de identidad
    Optional<Empleados> findByDni(String dni);

    // Para búsquedas rápidas en el buscador del sistema
    List<Empleados> findByApellidoContainingIgnoreCase(String apellido);

    // Buscar por celular
    Optional<Empleados> findByCelular(String celular);

    // Para listar a los empleados
    List<Empleados> findByEstado(EstadoEmpleado estado);

    // Lista empleados que aún no tienen usuario vinculado.
    // Usado por el combobox del formulario de creación de usuarios.
    @Query("SELECT e FROM Empleados e WHERE e.idEmpleado NOT IN (SELECT u.empleado.idEmpleado FROM Usuarios u)")
    List<Empleados> findEmpleadosSinUsuario();
}