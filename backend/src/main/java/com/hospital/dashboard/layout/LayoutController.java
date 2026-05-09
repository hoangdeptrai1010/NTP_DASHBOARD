package com.hospital.dashboard.layout;

import com.hospital.dashboard.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/layout")
@RequiredArgsConstructor
public class LayoutController {

    private final LayoutService layoutService;

    @GetMapping
    public LayoutPreference getLayout(Authentication authentication) {
        return layoutService.getCurrentLayout(((AuthUser) authentication).user());
    }

    @PutMapping
    public LayoutPreference saveLayout(@RequestBody LayoutPreference layoutPreference, Authentication authentication) {
        return layoutService.saveLayout(((AuthUser) authentication).user(), layoutPreference);
    }
}
