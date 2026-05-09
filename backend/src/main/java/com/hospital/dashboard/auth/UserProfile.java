package com.hospital.dashboard.auth;

public record UserProfile(
    Long id,
    String username,
    String fullName,
    String role,
    Long departmentId,
    String departmentName
) {
    public static UserProfile from(AppUser user, String departmentName) {
        return new UserProfile(
            user.getUserId(),
            user.getUsername(),
            user.getFullName(),
            user.getRole().getRoleCode(),
            user.getDepartmentId(),
            departmentName
        );
    }
}
