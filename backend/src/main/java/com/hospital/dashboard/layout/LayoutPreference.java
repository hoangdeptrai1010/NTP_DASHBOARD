package com.hospital.dashboard.layout;

import java.util.List;

public record LayoutPreference(
    List<String> widgets,
    String density
) {
}
