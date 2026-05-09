package com.hospital.dashboard.layout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.dashboard.auth.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LayoutService {

    private final LayoutPreferenceRepository layoutPreferenceRepository;
    private final ObjectMapper objectMapper;

    public LayoutPreference getCurrentLayout(AppUser user) {
        return layoutPreferenceRepository.findById(user.getUserId())
            .map(LayoutPreferenceEntity::getLayoutJson)
            .filter(json -> json != null && !json.isBlank())
            .map(this::fromJson)
            .orElse(new LayoutPreference(List.of("kpis", "revenue", "inventory"), "comfortable"));
    }

    public LayoutPreference saveLayout(AppUser user, LayoutPreference layoutPreference) {
        LayoutPreferenceEntity entity = layoutPreferenceRepository.findById(user.getUserId())
            .orElseGet(() -> {
                LayoutPreferenceEntity created = new LayoutPreferenceEntity();
                created.setUserId(user.getUserId());
                return created;
            });
        entity.setLayoutJson(toJson(layoutPreference));
        layoutPreferenceRepository.save(entity);
        return layoutPreference;
    }

    private LayoutPreference fromJson(String json) {
        try {
            return objectMapper.readValue(json, LayoutPreference.class);
        } catch (Exception ex) {
            return new LayoutPreference(List.of("kpis", "revenue", "inventory"), "comfortable");
        }
    }

    private String toJson(LayoutPreference layoutPreference) {
        try {
            return objectMapper.writeValueAsString(layoutPreference);
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot persist layout preference.", ex);
        }
    }
}
