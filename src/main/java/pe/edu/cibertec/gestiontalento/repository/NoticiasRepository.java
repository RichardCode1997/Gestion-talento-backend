package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Noticias;
import java.util.List;

public interface NoticiasRepository extends JpaRepository<Noticias, Integer> {
    // Para mostrar solo anuncios vigentes en el dashboard
    List<Noticias> findByEstado(String estado);
}