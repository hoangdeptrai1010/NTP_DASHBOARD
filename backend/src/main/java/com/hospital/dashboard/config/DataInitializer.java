package com.hospital.dashboard.config;

import com.hospital.dashboard.auth.AppRole;
import com.hospital.dashboard.auth.AppRoleRepository;
import com.hospital.dashboard.auth.AppUser;
import com.hospital.dashboard.auth.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // 1. Create default Role if not exists
            AppRole adminRole = roleRepository.findByRoleCode("DIRECTOR")
                .orElseGet(() -> {
                    AppRole role = new AppRole();
                    role.setRoleId(1L);
                    role.setRoleCode("DIRECTOR");
                    role.setRoleName("Giám đốc");
                    return roleRepository.save(role);
                });

            // 2. Create default Admin User if not exists
            String username = "admin";
            if (userRepository.findByUsernameIgnoreCase(username).isEmpty()) {
                System.out.println(">>> Initializing default admin user...");
                AppUser admin = new AppUser();
                admin.setUserId(1L);
                admin.setUsername(username);
                admin.setPasswordHash(passwordEncoder.encode("admin"));
                admin.setFullName("Administrator");
                admin.setRole(adminRole);
                admin.setActiveFlag(1);
                userRepository.save(admin);
                System.out.println(">>> Default admin user created successfully.");
            }
        };
    }
}
