package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.gestiontalento.model.Departamentos;
import java.util.Optional;

@Repository
public interface DepartamentosRepository extends JpaRepository<Departamentos, Integer> {

    // Útil para validar que no existan departamentos duplicados o buscarlos por nombre
    Optional<Departamentos> findByNombreDepartamento(String nombreDepartamento);
}