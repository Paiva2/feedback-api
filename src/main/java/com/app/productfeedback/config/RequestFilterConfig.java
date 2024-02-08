package com.app.productfeedback.config;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.productfeedback.entities.User;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.repositories.UserRepositoryImpl;
import com.app.productfeedback.services.jwt.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("null")
@Component
public class RequestFilterConfig extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String getToken = this.retrieveHeaderToken(request);

        if (getToken != null) {
            String retrieveToken = this.jwtService.verify(getToken);

            UUID userId = UUID.fromString(retrieveToken);

            Optional<User> user = this.userRepositoryImpl.findById(userId);

            if (user.isEmpty()) {
                throw new NotFoundException("Token error - User not found.");
            }

            var authentication = new UsernamePasswordAuthenticationToken(user, retrieveToken, null);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    public String retrieveHeaderToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null) {
            return null;
        }

        return authHeader.replaceAll("Bearer", "");
    }
}
