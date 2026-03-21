package pe.edu.cibertec.gestiontalento.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Agregamos esto para que Spring pueda inyectarlo automáticamente
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUsersDetailsService customUsersDetailsService;

    @Autowired
    private JwtGenerador jwtGenerador;

    private String obtenerTokenDeSolicitud(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Simplificado: desde el índice 7 hasta el final
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraemos el token de la cabecera
        String token = obtenerTokenDeSolicitud(request);

        // 2. Validamos el token usando el generador actualizado
        if (StringUtils.hasText(token) && jwtGenerador.validarToken(token)) {

            // 3. Obtenemos el nombre de usuario (correo) del token
            String username = jwtGenerador.obtenerUsernameDeJwt(token);

            // 4. Cargamos los datos del usuario desde la BD
            UserDetails userDetails = customUsersDetailsService.loadUserByUsername(username);

            // 5. Creamos el objeto de autenticación
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 6. Seteamos la seguridad en el contexto de Spring
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 7. Continuamos con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}