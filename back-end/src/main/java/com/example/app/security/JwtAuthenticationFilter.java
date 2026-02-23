package com.example.app.security;

import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // No token → maybe public endpoint
        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Malformed header → reject
        if (!authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "MALFORMED_AUTH_HEADER");
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            // Extract claims
            String userIdStr = jwtService.extractUserId(jwt);
            String email = jwtService.extractEmail(jwt);

            if (userIdStr == null || email == null) {
                sendUnauthorized(response, "INVALID_TOKEN");
                return;
            }

            // Already authenticated → continue
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Parse userId
            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                sendUnauthorized(response, "INVALID_TOKEN");
                return;
            }

            // Load user
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                sendUnauthorized(response, "USER_NOT_FOUND");
                return;
            }

            if (user.getStatus().equals("banned")) {
                sendUnauthorized(response, "USER_BANNED");
                return;
            }

            // Validate token (signature, expiration, user match)
            if (!jwtService.isTokenValid(jwt, user)) {
                sendUnauthorized(response, "INVALID_TOKEN");
                return;
            }

            // Authenticate user
            UserPrincipal principal = new UserPrincipal(user);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Continue
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Covers expired, malformed, tampered JWTs
            sendUnauthorized(response, "INVALID_TOKEN");
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}

