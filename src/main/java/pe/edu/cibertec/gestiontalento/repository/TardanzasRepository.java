package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Tardanzas;
import java.util.List;

public interface TardanzasRepository extends JpaRepository<Tardanzas, Integer> {
    // Para listar las tardanzas de un empleado específico
    List<Tardanzas> findByEmpleadoIdEmpleado(int idEmpleado);
}