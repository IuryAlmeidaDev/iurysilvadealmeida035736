package com.iury.backendsenior.config;

import lombok.RequiredArgsConstructor;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final DistributedRateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    // Preflight
                    req.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // WebSocket handshake (STOMP)
                    req.requestMatchers("/ws-albuns/**").permitAll();

                    // Observabilidade
                    req.requestMatchers("/actuator/**").permitAll();

                    // Documentação
                    req.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll();

                    // Autenticação
                    req.requestMatchers(HttpMethod.POST, "/v1/auth/login", "/v1/auth/register", "/v1/auth/refresh")
                            .permitAll();

                    // Catálogo público (somente GET)
                    req.requestMatchers(HttpMethod.GET,
                            "/v1/albuns/**",
                            "/v1/artistas/**",
                            "/v1/musicas/**"
                    ).permitAll();

                    // Regionais: tudo protegido (GET e POST, inclusive /sincronizar)
                    req.requestMatchers("/v1/regionais/**").authenticated();

                    // Restante: protegido
                    req.anyRequest().authenticated();
                })
                
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
