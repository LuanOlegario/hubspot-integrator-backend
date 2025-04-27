package br.com.meetime.hubspotintegrator.config;

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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/oauth/**").permitAll()
                        .requestMatchers("/authorization.html").permitAll()
                        .requestMatchers("/create-contact.html").permitAll() //TODO MELHORIA, PEGAR O TOKEN E SÓ DEIXAR ACESSAR SE O USER ESTIVER LOGADO
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}