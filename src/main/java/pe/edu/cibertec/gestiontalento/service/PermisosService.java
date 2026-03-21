package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Permisos;
import pe.edu.cibertec.gestiontalento.repository.PermisosRepository;

import java.util.List;

@Service
public class PermisosService {

    private final PermisosRepository permisosRepository;

    @Autowired
    public PermisosService(PermisosRepository permisosRepository) {
        this.permisosRepository = permisosRepository;
    }

    public Permisos crearPermiso(Permisos permiso) {
        return permisosRepository.save(permiso);
    }

    public List<Permisos> listarPermisos() {
        return permisosRepository.findAll();
    }

    public Permisos obtenerPermisoPorId(int id) {
        return permisosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el permiso con ID: " + id));
    }

    public Permisos actualizarPermiso(Permisos permiso) {
        obtenerPermisoPorId(permiso.getIdPermiso()); // Valida existencia
        return permisosRepository.save(permiso);
    }

    public void eliminarPermiso(int id) {
        Permisos permiso = obtenerPermisoPorId(id); // Valida existencia
        permisosRepository.delete(permiso);
    }
}