package com.hospital.dashboard.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.parse(token);
                String username = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                String roleCode = claims.get("role", String.class);
                Long departmentId = claims.get("departmentId", Long.class);
                String fullName = claims.get("fullName", String.class);

                if (username != null && roleCode != null) {
                    AppRole role = new AppRole();
                    role.setRoleCode(roleCode);

                    AppUser user = new AppUser();
                    user.setUserId(userId);
                    user.setUsername(username);
                    user.setRole(role);
                    user.setDepartmentId(departmentId);
                    user.setFullName(fullName);
                    user.setActiveFlag(1);

                    SecurityContextHolder.getContext().setAuthentication(new AuthUser(user));
                }
            } catch (JwtException | AuthException ignored) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
