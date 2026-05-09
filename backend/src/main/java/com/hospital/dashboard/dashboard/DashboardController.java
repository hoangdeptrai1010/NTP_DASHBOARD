package com.hospital.dashboard.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/snapshot")
    public DashboardSnapshotResponse getSnapshot(
        @RequestParam(defaultValue = "week") String period,
        @RequestParam(required = false) Long departmentId,
        Authentication authentication
    ) {
        return dashboardService.getSnapshot(period, departmentId, (com.hospital.dashboard.auth.AuthUser) authentication);
    }

    @GetMapping("/revenue")
    public RevenueResponse getRevenue(
        @RequestParam(defaultValue = "week") String period,
        @RequestParam(required = false) Long departmentId,
        Authentication authentication
    ) {
        return dashboardService.getRevenue(period, departmentId, (com.hospital.dashboard.auth.AuthUser) authentication);
    }

    @GetMapping("/analysis")
    public AnalysisResponse getAnalysis(
        @RequestParam(defaultValue = "week") String period,
        @RequestParam(required = false) Long departmentId,
        Authentication authentication
    ) {
        return dashboardService.getAnalysis(period, departmentId, (com.hospital.dashboard.auth.AuthUser) authentication);
    }
}
