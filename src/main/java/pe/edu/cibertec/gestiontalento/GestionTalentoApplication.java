package pe.edu.cibertec.gestiontalento;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.cibertec.gestiontalento.model.Roles;
import pe.edu.cibertec.gestiontalento.model.Usuarios;
import pe.edu.cibertec.gestiontalento.repository.RolesRepository;
import pe.edu.cibertec.gestiontalento.repository.UsuariosRepository;

@SpringBootApplication
public class GestionTalentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionTalentoApplication.class, args);
	}
	@Bean
	CommandLineRunner init(UsuariosRepository usuariosRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (rolesRepository.count() == 0) {
				// 1. Crear Rol
				Roles adminRol = new Roles();
				adminRol.setNombreRol("ADMINISTRADOR");
				rolesRepository.save(adminRol);

				// 2. Crear Usuario (SOLO con los campos que tiene tu entidad ahora)
				Usuarios admin = new Usuarios();
				admin.setCorreo("admin@correo.com");
				// Encriptamos la contraseña "123"
				admin.setContraseña(passwordEncoder.encode("123"));
				admin.setRol(adminRol);

				usuariosRepository.save(admin);

				System.out.println("--------------------------------------");
				System.out.println("USUARIO BASE CREADO: admin@correo.com / 123");
				System.out.println("--------------------------------------");
			}
		};
	}
}
