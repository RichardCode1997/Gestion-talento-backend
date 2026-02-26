package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Usuarios;

public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
}
