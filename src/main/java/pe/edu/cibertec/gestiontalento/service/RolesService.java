package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Roles;
import pe.edu.cibertec.gestiontalento.repository.RolesRepository;

import java.util.List;

@Service
public class RolesService {

    private final RolesRepository rolesRepository;

    @Autowired
    public RolesService(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    public Roles crearRol(Roles rol) {
        return rolesRepository.save(rol);
    }

    public List<Roles> listarRoles() {
        return rolesRepository.findAll();
    }

    public Roles obtenerRolPorId(int id) {
        return rolesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el rol con ID: " + id));
    }

    // Nuevo: Útil para buscar roles por nombre (ej. ROLE_ADMIN)
    public Roles obtenerRolPorNombre(String nombre) {
        return rolesRepository.findByNombreRol(nombre)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el rol: " + nombre));
    }

    public Roles actualizarRol(Roles rol) {
        obtenerRolPorId(rol.getIdRol()); // Valida existencia
        return rolesRepository.save(rol);
    }

    public void eliminarRol(int id) {
        Roles rol = obtenerRolPorId(id); // Valida existencia
        rolesRepository.delete(rol);
    }
}