package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Empleados;

public interface EmpleadosRepository extends JpaRepository<Empleados, Integer> {
}
