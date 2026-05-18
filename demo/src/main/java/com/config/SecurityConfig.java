package com.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll() 
                .requestMatchers(HttpMethod.GET, "/**").permitAll() 
                
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/cadastrar").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/cadastrar-admin").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/redefinir-senha").permitAll()

                .anyRequest().authenticated()
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🌟 AQUI ESTÁ O NOSSO PASSAPORTE VIP GLOBAL PARA O CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Permite o 'null' do file:/// e qualquer outro site
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Permite todos os comandos
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite o cabeçalho Authorization com o Token
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}