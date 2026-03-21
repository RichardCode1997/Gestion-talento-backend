package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Horarios;
import pe.edu.cibertec.gestiontalento.repository.HorariosRepository;

import java.util.List;

@Service
public class HorariosService {

    private final HorariosRepository horariosRepository;

    @Autowired
    public HorariosService(HorariosRepository horariosRepository) {
        this.horariosRepository = horariosRepository;
    }

    public Horarios crearHorario(Horarios horario) {
        return horariosRepository.save(horario);
    }

    public List<Horarios> listarHorarios() {
        return horariosRepository.findAll();
    }

    public Horarios obtenerHorarioPorId(int id) {
        return horariosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el horario con ID: " + id));
    }

    public Horarios actualizarHorario(Horarios horario) {
        obtenerHorarioPorId(horario.getIdHorario()); // Valida existencia
        return horariosRepository.save(horario);
    }

    public void eliminarHorario(int id) {
        Horarios horario = obtenerHorarioPorId(id); // Valida existencia
        horariosRepository.delete(horario);
    }
}