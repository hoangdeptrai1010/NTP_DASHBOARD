package com.hospital.dashboard.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "APP_ROLES")
public class AppRole {

    @Id
    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ROLE_CODE", nullable = false, unique = true)
    private String roleCode;

    @Column(name = "ROLE_NAME", nullable = false)
    private String roleName;
}
