package com.hospital.dashboard.auth;

public record LoginResponse(
    String accessToken,
    UserProfile user
) {
}
