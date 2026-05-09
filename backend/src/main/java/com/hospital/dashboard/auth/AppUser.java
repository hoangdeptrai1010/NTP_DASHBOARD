package com.hospital.dashboard.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "APP_USERS")
public class AppUser {

    @Id
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Column(name = "FULLNAME", nullable = false)
    private String fullName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private AppRole role;

    @Column(name = "IDKHOAPHONG")
    private Long departmentId;

    @Column(name = "IS_ACTIVE")
    private Integer activeFlag;

    public boolean isActive() {
        return activeFlag == null || activeFlag == 1;
    }
}
