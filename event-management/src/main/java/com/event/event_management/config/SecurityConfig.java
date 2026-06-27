package com.event.event_management.config;

import com.event.event_management.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers("/dashboard/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/events/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.PUT, "/events/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")

                        .requestMatchers(HttpMethod.DELETE, "/events/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")

                        .requestMatchers("/events/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")

                        .requestMatchers("/registrations/export/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")

                        .requestMatchers("/teams/**")
                        .permitAll()

                        .requestMatchers("/attendance/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")

                        .requestMatchers("/registrations/**")
                        .hasRole("STUDENT")

                        .requestMatchers("/certificate/**")
                        .hasAnyRole("ADMIN", "ORGANIZER", "STUDENT")

                        .anyRequest()
                        .authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

