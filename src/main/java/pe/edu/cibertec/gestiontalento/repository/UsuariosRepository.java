package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.gestiontalento.model.Usuarios;

import java.util.List;
import java.util.Optional;

public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {

    // Login normal
    Optional<Usuarios> findByCorreo(String correo);

    // Login SEGURO: Solo encuentra al usuario si su estado es 1 (true)
    Optional<Usuarios> findByCorreoAndEstadoTrue(String correo);

    // Listado de "Solo Activos"
    List<Usuarios> findByEstadoTrue();

    // Listado de "Solo Inactivos"
    List<Usuarios> findByEstadoFalse();
}