package pe.edu.cibertec.gestiontalento.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter) { // <--- Inyéctalo aquí
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Nueva sintaxis para deshabilitar CSRF
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Público
                        .requestMatchers("/api/auth/**").permitAll()

                        // Solo ADMINISTRADOR
                        .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/horarios/**").hasRole("ADMINISTRADOR")

                        // Empleados: SUPERVISOR solo puede GET, ADMIN puede todo
                        .requestMatchers(HttpMethod.GET, "/api/empleados/**").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers("/api/empleados/**").hasRole("ADMINISTRADOR")

                        // ADMIN, SUPERVISOR, ASESOR y SISTEMAS
                        .requestMatchers("/api/asistencias/**").hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "ASESOR", "SISTEMAS")
                        .requestMatchers("/api/tardanzas/**").hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "ASESOR", "SISTEMAS")
                        .requestMatchers("/api/faltas/**").hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "ASESOR", "SISTEMAS")
                        .requestMatchers("/api/permisos/**").hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "ASESOR", "SISTEMAS")

                        // Todos los roles logueados
                        .requestMatchers("/api/noticias/**").authenticated()
                        .requestMatchers("/api/departamentos/**").authenticated()
                        .requestMatchers("/api/roles/**").authenticated()

                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // BUENA PRÁCTICA: En lugar de "*", usa el puerto exacto de tu Vue (Vite suele ser 5173)
        // Esto evita ataques de Cross-Site Request Forgery (CSRF)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));

        // PERMITIR CREDENCIALES: Si usas JWT en Cookies o Headers específicos
        configuration.setAllowCredentials(true);

        // EXPOSE HEADERS: Si el backend genera el token y lo manda en el Header,
        // Vue necesita permiso explícito para leerlo.
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}