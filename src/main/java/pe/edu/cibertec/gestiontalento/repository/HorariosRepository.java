package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.gestiontalento.model.Horarios;
import java.util.Optional;

@Repository
public interface HorariosRepository extends JpaRepository<Horarios, Integer> {

    // Útil para buscar un horario específico por su nombre descriptivo
    Optional<Horarios> findByNombre(String nombre);
}