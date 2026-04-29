package bootcamp07.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilitar CSRF para facilitar testes da API
            .csrf(csrf -> csrf.disable())

            // Configurar permissões - permitir tudo por enquanto
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // Se usar H2
                .requestMatchers("/actuator/**").permitAll() // Spring Boot Actuator
                .requestMatchers("/api/**").permitAll() // Permitir todas as APIs
                .anyRequest().permitAll() // Permitir tudo para facilitar desenvolvimento
            )

            // Configuração de sessão
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
