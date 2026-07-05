package com.kazakov.catalog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()

                        // 2. Для любых других методов (POST, PUT, DELETE) требуем авторизацию
                        .requestMatchers(HttpMethod.POST,"/api/v1/products", "/api/v1/products/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/api/v1/products", "/api/v1/products/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/products", "/api/v1/products/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/v1/categories/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").authenticated()

                        // На всякий случай закрываем всё остальное
                        .anyRequest().authenticated()
                )
                // Добавляем наш внутренний фильтр перед стандартным фильтром Спринга
                .addFilterBefore(new InternalAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
