package com.fooddelivery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.dto.ErrorResponseDto;
import com.fooddelivery.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

import java.time.LocalDateTime;

@Configuration
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final ObjectMapper objectMapper;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        ObjectMapper objectMapper) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.objectMapper = objectMapper;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())

                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.deny())
                                                .xssProtection(xss -> xss.disable()) // Handled by modern browsers &
                                                                                     // Content-Security-Policy
                                                .contentTypeOptions(Customizer.withDefaults()))

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                // Public Auth APIs
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/api/webhooks/**").permitAll()

                                                // Public Browsing APIs
                                                .requestMatchers(HttpMethod.GET, "/api/restaurants/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/menu/**").permitAll()

                                                // Admin Only
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/api/restaurants/manage/**").hasRole("ADMIN")
                                                .requestMatchers("/api/menu/manage/**").hasRole("ADMIN")

                                                // Owner Only
                                                .requestMatchers("/api/owner/**").hasRole("RESTAURANT_OWNER")

                                                // Delivery Only
                                                .requestMatchers("/api/delivery/**").hasRole("DELIVERY_PARTNER")

                                                // Protected Commerce APIs
                                                .requestMatchers("/api/cart/**").hasAnyRole("CUSTOMER", "ADMIN")
                                                .requestMatchers("/api/orders/**").hasAnyRole("CUSTOMER", "ADMIN")
                                                .requestMatchers("/api/payments/**").hasAnyRole("CUSTOMER", "ADMIN")

                                                // Default Protection
                                                .anyRequest().authenticated())

                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.setContentType("application/json");
                                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                                                        ErrorResponseDto errorResponse = new ErrorResponseDto(
                                                                        LocalDateTime.now(),
                                                                        HttpStatus.UNAUTHORIZED.value(),
                                                                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                                                        "Authentication is required",
                                                                        request.getRequestURI());

                                                        objectMapper.writeValue(response.getOutputStream(),
                                                                        errorResponse);
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.setContentType("application/json");
                                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                                                        ErrorResponseDto errorResponse = new ErrorResponseDto(
                                                                        LocalDateTime.now(),
                                                                        HttpStatus.FORBIDDEN.value(),
                                                                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                                                                        "Access Denied: You do not have permission to access this resource.",
                                                                        request.getRequestURI());

                                                        objectMapper.writeValue(response.getOutputStream(),
                                                                        errorResponse);
                                                }))

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                .formLogin(form -> form.disable())

                                .httpBasic(basic -> basic.disable());

                return http.build();
        }

        @Bean
        public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
                org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
                // In production, these should be loaded from application.properties
                configuration.setAllowedOrigins(java.util.List.of("http://localhost:5173", "http://127.0.0.1:5173"));
                configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "X-Requested-With"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}