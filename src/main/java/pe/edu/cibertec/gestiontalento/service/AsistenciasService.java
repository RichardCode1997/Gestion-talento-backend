package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Asistencias;
import pe.edu.cibertec.gestiontalento.repository.AsistenciasRepository;

import java.util.List;

@Service
public class AsistenciasService {

    private final AsistenciasRepository asistenciasRepository;

    @Autowired
    public AsistenciasService(AsistenciasRepository asistenciasRepository) {
        this.asistenciasRepository = asistenciasRepository;
    }

    public Asistencias crearAsistencia(Asistencias asistencia) {
        return asistenciasRepository.save(asistencia);
    }

    public List<Asistencias> listarAsistencias() {
        return asistenciasRepository.findAll();
    }

    public Asistencias obtenerAsistenciaPorId(int id) {
        return asistenciasRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la asistencia con ID: " + id));
    }

    public Asistencias actualizarAsistencia(Asistencias asistencia) {
        obtenerAsistenciaPorId(asistencia.getIdAsistencia()); // Valida existencia
        return asistenciasRepository.save(asistencia);
    }

    public void eliminarAsistencia(int id) {
        Asistencias asistencia = obtenerAsistenciaPorId(id); // Valida existencia
        asistenciasRepository.delete(asistencia);
    }
}