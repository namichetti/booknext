package com.amichettinestor.booknext.booknext.config.security;

import com.amichettinestor.booknext.booknext.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // Me permite usar Pre y PostAuthorization.
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests ->requests
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/auth/confirm","/api/v1/author/**","/api/v1/bookcategory/**","/api/v1/book/**","/api/v1/order","/api/v1/publisher/**")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/webjars/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/user/me","/api/v1/order/**")
                        .hasAnyRole("ADMIN","USER","MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/user/me/users/**")
                        .hasAnyRole("ADMIN","MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/book","/api/v1/bookcategory","/api/v1/author")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/order")
                        .hasAnyRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/book/**","/api/v1/bookcategory/**","/api/v1/author/**","/api/v1/author","/api/v1/publisher/**")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/order/**")
                        .hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/user/me","/api/v1/user/me/username","/api/v1/user/me/password","/api/v1/user/me/email")
                        .hasAnyRole("ADMIN","USER","MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/user/admin/**")
                        .hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/user/manager/**","/api/v1/order/manager/**")
                        .hasAnyRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/book/**","/api/v1/bookcategory/**","/api/v1/author/**","/api/v1/author","/api/v1/publisher")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/order/**")
                        .hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/order/**")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/book/**","/api/v1/bookcategory/**","/api/v1/author/**","/api/v1/author","/api/v1/publisher/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
