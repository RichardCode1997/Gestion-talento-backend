package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Usuarios;
import pe.edu.cibertec.gestiontalento.repository.UsuariosRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuariosService(UsuariosRepository usuariosRepository, PasswordEncoder passwordEncoder) {
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuarios crearUsuario(Usuarios usuario) {
        // Encriptamos la contraseña antes de guardarla
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        return usuariosRepository.save(usuario);
    }

    public List<Usuarios> listarUsuarios() {
        return usuariosRepository.findAll();
    }

    public Usuarios obtenerUsuarioPorId(int id) {
        Optional<Usuarios> usuarioOptional = usuariosRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            return usuarioOptional.get();
        } else {
            throw new IllegalArgumentException("No se encontró el usuario especificado.");
        }
    }

    public Usuarios modificarUsuario(int id, Usuarios usuarioModificado) {
        Optional<Usuarios> usuarioOptional = usuariosRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuarios usuarioExistente = usuarioOptional.get();
            // Actualizar los campos necesarios del usuario existente con los valores del usuario modificado
            usuarioExistente.setNombre(usuarioModificado.getNombre());
            usuarioExistente.setApellido(usuarioModificado.getApellido());
            usuarioExistente.setDni(usuarioModificado.getDni());
            usuarioExistente.setCorreo(usuarioModificado.getCorreo());
            usuarioExistente.setRol(usuarioModificado.getRol());
            if (usuarioModificado.getContraseña() != null && !usuarioModificado.getContraseña().isEmpty()) {
                usuarioExistente.setContraseña(passwordEncoder.encode(usuarioModificado.getContraseña()));
            }
            return usuariosRepository.save(usuarioExistente);
        } else {
            throw new IllegalArgumentException("No se encontró el usuario especificado.");
        }
    }

    public void eliminarUsuario(int id) {
        Optional<Usuarios> usuarioOptional = usuariosRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            usuariosRepository.delete(usuarioOptional.get());
        } else {
            throw new IllegalArgumentException("No se encontró el usuario especificado.");
        }
    }
}
