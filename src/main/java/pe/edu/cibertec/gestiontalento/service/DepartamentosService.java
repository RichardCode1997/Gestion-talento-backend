package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Departamentos;
import pe.edu.cibertec.gestiontalento.repository.DepartamentosRepository;

import java.util.List;

@Service
public class DepartamentosService {

    private final DepartamentosRepository departamentosRepository;

    @Autowired
    public DepartamentosService(DepartamentosRepository departamentosRepository) {
        this.departamentosRepository = departamentosRepository;
    }

    public Departamentos crearDepartamento(Departamentos departamento) {
        return departamentosRepository.save(departamento);
    }

    public List<Departamentos> listarDepartamentos() {
        return departamentosRepository.findAll();
    }

    public Departamentos obtenerDepartamentoPorId(int id) {
        return departamentosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el departamento con ID: " + id));
    }

    public Departamentos actualizarDepartamento(Departamentos departamento) {
        obtenerDepartamentoPorId(departamento.getIdDepartamento()); // Valida existencia
        return departamentosRepository.save(departamento);
    }

    public void eliminarDepartamento(int id) {
        Departamentos depto = obtenerDepartamentoPorId(id); // Valida existencia
        departamentosRepository.delete(depto);
    }
}