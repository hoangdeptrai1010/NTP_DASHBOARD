package com.hospital.dashboard.auth;

public record AuthPayload(
    LoginResponse response,
    String refreshToken
) {
}
