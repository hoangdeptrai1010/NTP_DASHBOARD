package com.hospital.dashboard.dashboard;

import java.util.List;

public record AnalysisResponse(
    List<RatioItem> treatmentRatio,
    List<RatioItem> paymentRatio,
    List<DeptCompareItem> topDepartments
) {
}
