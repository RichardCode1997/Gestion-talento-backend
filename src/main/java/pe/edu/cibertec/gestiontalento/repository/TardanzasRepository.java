package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Tardanzas;

public interface TardanzasRepository extends JpaRepository<Tardanzas, Integer> {
}
