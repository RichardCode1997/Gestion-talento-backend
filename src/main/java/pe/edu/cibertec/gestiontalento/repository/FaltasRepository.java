package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Faltas;

public interface FaltasRepository extends JpaRepository<Faltas, Integer> {
}
