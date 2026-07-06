package com.hospital.dashboard.layout;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LayoutPreferenceRepository extends JpaRepository<LayoutPreferenceEntity, Long> {
    Optional<LayoutPreferenceEntity> findByUserId(Long userId);
}
