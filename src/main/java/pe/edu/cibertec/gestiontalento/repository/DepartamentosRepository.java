package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Departamentos;

public interface DepartamentosRepository extends JpaRepository<Departamentos, Integer> {
}
