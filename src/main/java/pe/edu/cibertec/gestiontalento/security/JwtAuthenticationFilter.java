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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*La función de esta clase será validar la información del token y si esto es exitoso,
establecerá la autenticación de un usuario en la solicitud o en el contexto de seguridad de nuestra aplicación*/
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUsersDetailsService customUsersDetailsService;
    @Autowired
    private JwtGenerador jwtGenerador;

    /*Con el siguiente método extraeremos  el token JWT de la cabecera de nuestra petición Http("Authorization")
     * luego lo validaremos y finalmente se retornará*/
    private String obtenerTokenDeSolicitud(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            //Aca si se encuentra el token JWT, se devuelve una subcadena de "bearerToken" que comienza después de los primeros 7 caracteres hasta el final de la cadena
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraemos el token
        String token = obtenerTokenDeSolicitud(request);

        // 2. Validamos el token (con el cambio que hicimos en JwtGenerador para que no lance excepción)
        if (StringUtils.hasText(token) && jwtGenerador.validarToken(token)) {

            // 3. Obtenemos los detalles del usuario
            String username = jwtGenerador.obtenerUsernameDeJwt(token);
            UserDetails userDetails = customUsersDetailsService.loadUserByUsername(username);

            // 4. CREAMOS LA AUTENTICACIÓN (Sin filtrar por rol aquí)
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 5. LO REGISTRAMOS EN EL CONTEXTO DE SEGURIDAD
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 6. CONTINUAMOS CON LA CADENA (Si no hubo token, pasará como anónimo y SecurityConfig decidirá)
        filterChain.doFilter(request, response);
    }
}
