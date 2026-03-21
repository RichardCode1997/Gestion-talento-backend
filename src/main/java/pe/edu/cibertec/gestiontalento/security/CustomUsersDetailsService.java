package pe.edu.cibertec.gestiontalento.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Roles;
import pe.edu.cibertec.gestiontalento.model.Usuarios;
import pe.edu.cibertec.gestiontalento.repository.UsuariosRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUsersDetailsService implements UserDetailsService  {
    private UsuariosRepository usuariosRepo;

    @Autowired
    public CustomUsersDetailsService(UsuariosRepository usuariosRepo) {
        this.usuariosRepo = usuariosRepo;
    }
    //Metodo para traernos una lista de autoridades por medio de una lista de roles
    public Collection<GrantedAuthority> mapToAuthorities(Roles rol){
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol()));
    }


    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // 1. Buscamos solo usuarios que tengan estado = 1 (true)
        Usuarios usuario = usuariosRepo.findByCorreoAndEstadoTrue(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o cuenta desactivada"));

        // 2. Retornamos el UserDetails de Spring Security
        // Opcional: Puedes usar el constructor que acepta el booleano 'enabled'
        return new User(
                usuario.getCorreo(),
                usuario.getContraseña(),
                usuario.isEstado(), // enabled: true si el estado es 1
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                mapToAuthorities(usuario.getRol())
        );
    }
}
