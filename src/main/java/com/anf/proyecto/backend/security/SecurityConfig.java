package com.anf.proyecto.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF (Cross-Site Request Forgery) ya que usaremos una API stateless
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Definir las reglas de autorización para las peticiones HTTP
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir el acceso a TODOS los endpoints sin autenticación
                        .requestMatchers("/**").permitAll()
                );

        return http.build();
    }
}