package com.hospital.dashboard.dashboard;

public record KpiCardResponse(
    String key,
    String label,
    String value,
    String trend,
    String tone
) {
}
