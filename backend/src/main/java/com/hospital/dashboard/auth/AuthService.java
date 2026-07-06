package com.hospital.dashboard.auth;

import com.hospital.dashboard.department.DepartmentRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hospital.dashboard.config.AppProperties;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final String REFRESH_COOKIE = "refresh_token";

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AppProperties appProperties;

    public AuthPayload login(LoginRequest request) {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(request.username().trim())
            .filter(AppUser::isActive)
            .orElseThrow(() -> new AuthException("Invalid username or password."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthException("Invalid username or password.");
        }

        return issueTokens(user);
    }

    public AuthPayload refresh(String refreshToken) {
        Claims claims = jwtService.parse(refreshToken);
        if (!"refresh".equals(claims.get("tokenType", String.class))) {
            throw new AuthException("Invalid refresh token.");
        }

        String username = refreshTokenService.consume(refreshToken)
            .orElseThrow(() -> new AuthException("Refresh token expired or already used."));

        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
            .filter(AppUser::isActive)
            .orElseThrow(() -> new AuthException("User not found."));

        return issueTokens(user);
    }

    public void logout(String refreshToken) {
        if (refreshToken != null) {
            refreshTokenService.consume(refreshToken);
        }
    }

    public ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_COOKIE, refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/api/auth")
            .maxAge(appProperties.jwt().refreshTokenDays() * 24 * 60 * 60)
            .build();
    }

    public ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE, "")
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/api/auth")
            .maxAge(0)
            .build();
    }

    private AuthPayload issueTokens(AppUser user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        refreshTokenService.store(user.getUsername(), refreshToken);
        return new AuthPayload(new LoginResponse(accessToken, buildUserProfile(user)), refreshToken);
    }

    private UserProfile buildUserProfile(AppUser user) {
        String departmentName = user.getDepartmentId() == null ? "Toàn viện" : departmentRepository.findById(user.getDepartmentId())
            .map(department -> department.getName())
            .orElse("Không xác định");
        return UserProfile.from(user, departmentName);
    }
}
