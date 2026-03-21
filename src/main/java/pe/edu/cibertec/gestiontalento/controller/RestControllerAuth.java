package pe.edu.cibertec.gestiontalento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.cibertec.gestiontalento.dtos.DtoAuthRespuesta;
import pe.edu.cibertec.gestiontalento.dtos.DtoLogin;
import pe.edu.cibertec.gestiontalento.security.JwtGenerador;

@RestController
@RequestMapping("/api/auth") // Eliminé la barra final innecesaria
public class RestControllerAuth {

    private final AuthenticationManager authenticationManager;
    private final JwtGenerador jwtGenerador;

    @Autowired
    public RestControllerAuth(AuthenticationManager authenticationManager, JwtGenerador jwtGenerador) {
        this.authenticationManager = authenticationManager;
        this.jwtGenerador = jwtGenerador;
    }

    @PostMapping("/login")
    public ResponseEntity<DtoAuthRespuesta> login(@RequestBody DtoLogin dtoLogin) {
        // Autentica usando el correo como username
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dtoLogin.getCorreo(), dtoLogin.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerador.generarToken(authentication);

        return ResponseEntity.ok(new DtoAuthRespuesta(token));
    }
}