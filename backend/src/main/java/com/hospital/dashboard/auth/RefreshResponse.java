package com.hospital.dashboard.auth;

public record RefreshResponse(
    String accessToken,
    UserProfile user
) {
}
