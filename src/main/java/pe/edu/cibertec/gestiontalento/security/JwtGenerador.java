package pe.edu.cibertec.gestiontalento.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtGenerador {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // NUEVO: Metodo para convertir tu String de firma en una Key real
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken(Authentication authentication) {
        String correo = authentication.getName();
        Date tiempoActual = new Date();
        Date expiracionToken = new Date(tiempoActual.getTime() + ConstantesSeguridad.JWT_EXPIRATION_TOKEN);

        // Generar el token
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(expiracionToken)
                // CAMBIO: Ahora se pasa primero la Key y luego el algoritmo
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String obtenerUsernameDeJwt(String token) {
        // CAMBIO: Usamos parserBuilder() en lugar de parser()
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Boolean validarToken(String token) {
        try {
            // CAMBIO: Usamos parserBuilder() aquí también
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}