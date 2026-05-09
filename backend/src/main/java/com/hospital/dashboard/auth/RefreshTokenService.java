package com.hospital.dashboard.auth;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenService {

    private final Map<String, String> refreshTokenIndex = new ConcurrentHashMap<>();

    public void store(String username, String refreshToken) {
        refreshTokenIndex.put(refreshToken, username);
    }

    public Optional<String> consume(String refreshToken) {
        var username = refreshTokenIndex.remove(refreshToken);
        return Optional.ofNullable(username);
    }
}
