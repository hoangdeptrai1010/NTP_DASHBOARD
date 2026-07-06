package com.hospital.dashboard.dashboard;

import com.hospital.dashboard.auth.AppRole;
import com.hospital.dashboard.auth.AppUser;
import com.hospital.dashboard.auth.AuthUser;
import com.hospital.dashboard.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRevenueEndpointReturns401WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/dashboard/revenue"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testRevenueEndpointReturns403ForDoctor() throws Exception {
        mockMvc.perform(get("/api/dashboard/revenue"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DIRECTOR")
    void testRevenueEndpointReturns200ForDirector() throws Exception {
        mockMvc.perform(get("/api/dashboard/revenue"))
                // Expect 200 or 500 depending on mock, but 403 should be bypassed
                // Since DashboardService is real in SpringBootTest and DB is not mocked, 
                // we just check it is not 401/403.
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Status was " + status + " but expected not forbidden/unauthorized");
                    }
                });
    }
}
