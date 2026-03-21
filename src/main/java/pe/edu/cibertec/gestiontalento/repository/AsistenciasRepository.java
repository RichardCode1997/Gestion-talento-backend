package pe.edu.cibertec.gestiontalento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.gestiontalento.model.Asistencias;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciasRepository extends JpaRepository<Asistencias, Integer> {

    // Para obtener el historial de un empleado (útil para su perfil)
    List<Asistencias> findByEmpleadoIdEmpleado(int idEmpleado);

    // Crítico para el marcado de salida: busca la asistencia del día actual sin hora de salida
    Optional<Asistencias> findByEmpleadoIdEmpleadoAndFechaAndHoraSalidaIsNull(int idEmpleado, String fecha);

    // Para reportes generales por día
    List<Asistencias> findByFecha(String fecha);
}