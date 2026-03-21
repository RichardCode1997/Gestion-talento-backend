package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Faltas;
import pe.edu.cibertec.gestiontalento.repository.FaltasRepository;

import java.util.List;

@Service
public class FaltasService {

    private final FaltasRepository faltasRepository;

    @Autowired
    public FaltasService(FaltasRepository faltasRepository) {
        this.faltasRepository = faltasRepository;
    }

    public Faltas crearFalta(Faltas falta) {
        return faltasRepository.save(falta);
    }

    public List<Faltas> listarFaltas() {
        return faltasRepository.findAll();
    }

    // Útil para reportes individuales
    public List<Faltas> listarPorEmpleado(int idEmpleado) {
        return faltasRepository.findByEmpleadoIdEmpleado(idEmpleado);
    }

    public Faltas obtenerFaltaPorId(int id) {
        return faltasRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la falta con ID: " + id));
    }

    public Faltas actualizarFalta(Faltas falta) {
        obtenerFaltaPorId(falta.getIdFalta());
        return faltasRepository.save(falta);
    }

    public void eliminarFalta(int id) {
        Faltas falta = obtenerFaltaPorId(id);
        faltasRepository.delete(falta);
    }
}