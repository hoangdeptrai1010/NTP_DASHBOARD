package com.hospital.dashboard.auth;

import com.hospital.dashboard.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final AppProperties appProperties;

    public JwtService(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.secretKey = Keys.hmacShaKeyFor(appProperties.jwt().secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(AppUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(user.getUsername())
            .claim("role", user.getRole().getRoleCode())
            .claim("departmentId", user.getDepartmentId())
            .claim("fullName", user.getFullName())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(appProperties.jwt().accessTokenMinutes(), ChronoUnit.MINUTES)))
            .signWith(secretKey)
            .compact();
    }

    public String generateRefreshToken(AppUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(user.getUsername())
            .claim("tokenType", "refresh")
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(appProperties.jwt().refreshTokenDays(), ChronoUnit.DAYS)))
            .signWith(secretKey)
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
