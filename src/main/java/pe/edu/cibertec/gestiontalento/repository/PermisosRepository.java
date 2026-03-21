package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Permisos;
import java.util.List;

public interface PermisosRepository extends JpaRepository<Permisos, Integer> {
    // Para ver el historial de permisos de un trabajador
    List<Permisos> findByEmpleadoIdEmpleado(int idEmpleado);
}