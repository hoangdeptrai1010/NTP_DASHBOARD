package com.hospital.dashboard.layout;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "DASHBOARD_LAYOUT_PREFS")
public class LayoutPreferenceEntity {

    @Id
    @Column(name = "USER_ID")
    private Long userId;

    @Lob
    @Column(name = "LAYOUT_JSON")
    private String layoutJson;
}
