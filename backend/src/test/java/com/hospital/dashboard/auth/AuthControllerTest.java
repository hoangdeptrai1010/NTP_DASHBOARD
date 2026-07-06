package com.hospital.dashboard.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void testRefreshFailsWithoutCsrfHeader() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                .cookie(new Cookie("refresh_token", "dummy_token")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRefreshSucceedsWithCsrfHeader() throws Exception {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        AppRole role = new AppRole();
        role.setRoleCode("DIRECTOR");
        user.setRole(role);
        
        AuthPayload dummyPayload = new AuthPayload(
            new LoginResponse("access", UserProfile.from(user, "test")), 
            "new_refresh"
        );
        
        when(authService.refresh(anyString())).thenReturn(dummyPayload);
        when(authService.buildRefreshCookie(anyString())).thenReturn(
            org.springframework.http.ResponseCookie.from("refresh_token", "test").build()
        );

        mockMvc.perform(post("/api/auth/refresh")
                .header("X-Requested-With", "XMLHttpRequest")
                .cookie(new Cookie("refresh_token", "dummy_token")))
                .andExpect(status().isOk());
    }

    @Test
    void testLogoutFailsWithoutCsrfHeader() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .cookie(new Cookie("refresh_token", "dummy_token")))
                .andExpect(status().isForbidden());
    }
}
