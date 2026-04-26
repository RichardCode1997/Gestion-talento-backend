package pe.edu.cibertec.gestiontalento.security;

import jakarta.servlet.http.HttpServletResponse;
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
    private final RateLimitingFilter rateLimitingFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          RateLimitingFilter rateLimitingFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimitingFilter = rateLimitingFilter;
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
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        // 401 - No autenticado (sin token o token inválido)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        // 403 - Autenticado pero sin permisos suficientes
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"error\": \"Acceso denegado: no tienes permisos para realizar esta acción.\"}"
                            );
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Público
                        .requestMatchers("/api/auth/**").permitAll()

                        // Usuarios: SUPERVISOR solo puede GET, ADMIN puede todo
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("SUPERADMIN", "ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers("/api/usuarios/**").hasAnyRole("SUPERADMIN", "ADMINISTRADOR")

                        // Empleados: SUPERVISOR solo puede GET, ADMIN puede todo
                        .requestMatchers(HttpMethod.GET, "/api/empleados/**").hasAnyRole("SUPERADMIN", "ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers("/api/empleados/**").hasAnyRole("SUPERADMIN", "ADMINISTRADOR")

                        // Horarios: SUPERVISOR solo puede GET, ADMIN puede todo
                        .requestMatchers(HttpMethod.GET, "/api/horarios/**").hasAnyRole("SUPERADMIN", "ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers("/api/horarios/**").hasAnyRole("SUPERADMIN", "ADMINISTRADOR")

                        // ADMIN, SUPERVISOR, ASESOR y SISTEMAS
                        .requestMatchers("/api/asistencias/**").hasAnyRole("SUPERADMIN", "ADMINISTRADOR", "SUPERVISOR", "ASESOR", "SISTEMAS")
                        .requestMatchers("/api/tardanzas/**").hasAnyRole("SUPERADMIN", "ADMINISTRADOR", "SUPERVISOR", "ASESOR", "SISTEMAS")
                        .requestMatchers("/api/faltas/**").hasAnyRole("SUPERADMIN", "ADMINISTRADOR", "SUPERVISOR", "ASESOR", "SISTEMAS")
                        .requestMatchers("/api/permisos/**").hasAnyRole("SUPERADMIN", "ADMINISTRADOR", "SUPERVISOR", "ASESOR", "SISTEMAS")

                        // Todos los roles logueados
                        .requestMatchers("/api/noticias/**").authenticated()
                        .requestMatchers("/api/departamentos/**").authenticated()
                        .requestMatchers("/api/roles/**").authenticated()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://gestion-talento-frontend-swart.vercel.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}