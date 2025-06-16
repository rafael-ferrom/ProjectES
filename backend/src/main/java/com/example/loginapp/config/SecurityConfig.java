package com.example.loginapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
            // Aplica a configuração de CORS
            .cors(withDefaults())
            // Desabilita a proteção CSRF
            .csrf(csrf -> csrf.disable())

            // Configura as regras de autorização
            .authorizeHttpRequests(auth -> auth
                // >>>>> ALTERAÇÃO PRINCIPAL AQUI <<<<<
                // Permite TODAS as requisições sem autenticação.
                .anyRequest().permitAll()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite requisições da URL do seu frontend
        configuration.setAllowedOrigins(List.of("https://techpharmacy.bounceme.net", "http://localhost:8081"));
        // Permite os métodos HTTP mais comuns
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permite cabeçalhos HTTP comuns
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuração a todas as rotas da sua API
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}