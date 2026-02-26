package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Roles;

public interface RolesRepository extends JpaRepository<Roles, Integer> {
}
