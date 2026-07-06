package com.hospital.dashboard.dashboard;

import com.hospital.dashboard.auth.AppRole;
import com.hospital.dashboard.auth.AppUser;
import com.hospital.dashboard.auth.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private DashboardService dashboardService;

    private AuthUser directorAuth;
    private AuthUser doctorAuth;

    @BeforeEach
    void setUp() {
        AppUser director = new AppUser();
        AppRole dirRole = new AppRole();
        dirRole.setRoleCode("DIRECTOR");
        director.setRole(dirRole);
        directorAuth = new AuthUser(director);

        AppUser doctor = new AppUser();
        AppRole docRole = new AppRole();
        docRole.setRoleCode("DOCTOR");
        doctor.setRole(docRole);
        doctor.setDepartmentId(5L);
        doctorAuth = new AuthUser(doctor);
    }

    @Test
    void testGetRevenueDeniedForDoctor() {
        assertThrows(AccessDeniedException.class, () -> 
            dashboardService.getRevenue("week", null, doctorAuth)
        );
    }

    @Test
    void testGetRevenueAllowedForDirector() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
            .thenReturn(List.of(new RevenuePointResponse("10/2023", 1000L)));

        RevenueResponse response = dashboardService.getRevenue("week", null, directorAuth);

        assertNotNull(response);
        assertEquals(1, response.points().size());
        assertEquals(1000L, response.points().get(0).amount());
    }

    @Test
    void testDoctorRestrictedToOwnDepartment() {
        AppUser doctorNoDept = new AppUser();
        AppRole docRole = new AppRole();
        docRole.setRoleCode("DOCTOR");
        doctorNoDept.setRole(docRole);
        AuthUser doctorNoDeptAuth = new AuthUser(doctorNoDept);

        assertThrows(AccessDeniedException.class, () -> 
            dashboardService.getAnalysis("week", 10L, doctorNoDeptAuth)
        );
    }
}
