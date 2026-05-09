package com.hospital.dashboard.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

public class AuthUser extends UsernamePasswordAuthenticationToken {

    public AuthUser(AppUser user) {
        super(user, null, List.of(() -> "ROLE_" + user.getRole().getRoleCode()));
    }

    public AppUser user() {
        return (AppUser) getPrincipal();
    }
}
