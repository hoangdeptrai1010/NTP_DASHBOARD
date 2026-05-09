package com.hospital.dashboard.dashboard;

import java.util.List;

public record RevenueResponse(
    String period,
    Long departmentId,
    List<RevenuePointResponse> points
) {
}
