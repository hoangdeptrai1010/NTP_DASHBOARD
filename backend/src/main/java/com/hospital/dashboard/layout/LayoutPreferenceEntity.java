package com.hospital.dashboard.layout;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Getter
@Setter
@Entity
@Table(name = "DASHBOARD_LAYOUT_PREFS")
public class LayoutPreferenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PREF_ID")
    private Long prefId;

    @Column(name = "USER_ID", unique = true)
    private Long userId;

    @Lob
    @Column(name = "LAYOUT_JSON")
    private String layoutJson;
}
