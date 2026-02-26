package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Noticias;

public interface NoticiasRepository extends JpaRepository<Noticias, Integer> {
}
