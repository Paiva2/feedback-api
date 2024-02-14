package com.app.productfeedback.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Autowired
    private RequestFilterConfig requestFilterConfig;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] authNeededGets = {"/api/v1/user/profile"};
        String[] authNeededPatchs = {"/api/v1/user/update"};
        String[] authNeededDeletes = {"/api/v1/comment/remove/**"};

        String[] adminNeededPosts = {"/api/v1/category/**", "/api/v1/category"};
        String[] adminNeededDeletes = {"/api/v1/category/**", "/api/v1/category"};

        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(HttpMethod.POST, authNeededGets).authenticated();
                    authorize.requestMatchers(HttpMethod.PATCH, authNeededPatchs).authenticated();
                    authorize.requestMatchers(HttpMethod.DELETE, authNeededDeletes).authenticated();
                    authorize.requestMatchers(HttpMethod.POST, adminNeededPosts).hasRole("ADMIN");
                    authorize.requestMatchers(HttpMethod.DELETE, adminNeededDeletes)
                            .hasRole("ADMIN").anyRequest().permitAll();
                }).addFilterBefore(requestFilterConfig, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
