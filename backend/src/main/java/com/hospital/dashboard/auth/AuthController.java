package com.hospital.dashboard.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthPayload payload = authService.login(request);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authService.buildRefreshCookie(payload.refreshToken()).toString())
            .body(payload.response());
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
        @CookieValue(name = AuthService.REFRESH_COOKIE, required = false) String refreshToken,
        HttpServletRequest request
    ) {
        if (!"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            throw new AccessDeniedException("Missing CSRF token header.");
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException("Missing refresh token.");
        }
        var payload = authService.refresh(refreshToken);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authService.buildRefreshCookie(payload.refreshToken()).toString())
            .body(new RefreshResponse(payload.response().accessToken(), payload.response().user()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        if (!"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            throw new AccessDeniedException("Missing CSRF token header.");
        }
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AuthService.REFRESH_COOKIE.equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        authService.logout(refreshToken);
        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, authService.clearRefreshCookie().toString())
            .build();
    }
}
