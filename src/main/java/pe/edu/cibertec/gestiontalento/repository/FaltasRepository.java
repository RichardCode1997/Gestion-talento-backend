package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.gestiontalento.model.Faltas;
import java.util.List;

@Repository
public interface FaltasRepository extends JpaRepository<Faltas, Integer> {

    // Para obtener el historial de inasistencias de un empleado específico
    List<Faltas> findByEmpleadoIdEmpleado(int idEmpleado);

    // Útil para reportes mensuales: buscar faltas por una fecha específica
    List<Faltas> findByFecha(String fecha);
}