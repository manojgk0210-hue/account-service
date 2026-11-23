package com.example.accountservice.security;

import com.example.accountservice.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwt;
    private final ObjectMapper mapper;
    public JwtAuthenticationFilter(JwtUtil jwt, ObjectMapper mapper) { this.jwt = jwt; this.mapper = mapper; }

    @Override protected boolean shouldNotFilter(HttpServletRequest request) {
        // CORS preflights carry no JWT; they must reach Spring Security's CORS handling.
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ") || header.length() == 7) { chain.doFilter(request, response); return; }
        String token = header.substring(7);
        if (!jwt.isTokenValid(token)) { unauthorized(response); return; }
        String username = jwt.extractUsername(token);
        if (username == null || username.isBlank()) { unauthorized(response); return; }
        var auth = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }
    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(401); response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), new ErrorResponse(401, "Unauthorized", "Invalid or expired token"));
    }
}
