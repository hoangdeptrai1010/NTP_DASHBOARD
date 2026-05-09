package com.hospital.dashboard.dashboard;

import java.util.List;

public record DashboardSnapshotResponse(
    List<KpiCardResponse> kpis,
    RevenueResponse revenue,
    List<RatioItem> serviceMedicineRatio,
    List<DeptCompareItem> topDepartments
) {
}
