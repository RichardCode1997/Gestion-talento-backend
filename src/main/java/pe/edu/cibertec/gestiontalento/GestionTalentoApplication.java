package pe.edu.cibertec.gestiontalento;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.cibertec.gestiontalento.model.Roles;
import pe.edu.cibertec.gestiontalento.model.Usuarios;
import pe.edu.cibertec.gestiontalento.repository.IRolesRepository;
import pe.edu.cibertec.gestiontalento.repository.IUsuariosRepository;

@SpringBootApplication
public class GestionTalentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionTalentoApplication.class, args);
	}
	@Bean
	CommandLineRunner init(IUsuariosRepository usuariosRepository, IRolesRepository rolesRepository, PasswordEncoder passwordEncoder) {
		return args -> {

			// Solo creamos el usuario inicial para no quedarnn
			if (rolesRepository.count() == 0) {
				// 1. Crear Rol
				Roles adminRol = new Roles();
				adminRol.setNombreRol("ADMINISTRADOR");
				rolesRepository.save(adminRol);

				// 2. Crear Usuario
				Usuarios admin = new Usuarios();
				admin.setNombre("Admin");
				admin.setApellido("Sistema");
				admin.setCorreo("admin@correo.com");
				admin.setDni("12345678");
				// Importante: Usamos el encriptador para que la clave funcione
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
