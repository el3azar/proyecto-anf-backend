package com.anf.proyecto.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Habilitar y configurar CORS usando nuestro Bean personalizado
                .cors(withDefaults())

                // 2. Deshabilitar CSRF (Cross-Site Request Forgery)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Definir las reglas de autorización para las peticiones HTTP
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir el acceso a TODOS los endpoints sin autenticación
                        .requestMatchers("/**").permitAll()
                );

        return http.build();
    }

    // ¡NUEVO BEAN PARA CONFIGURAR CORS!
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (la URL de tu frontend de React)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cabeceras permitidas
        configuration.setAllowedHeaders(List.of("*"));

        // Permitir credenciales (cookies, tokens de autorización)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", configuration); // Aplica la configuración a tus endpoints

        return source;
    }
}