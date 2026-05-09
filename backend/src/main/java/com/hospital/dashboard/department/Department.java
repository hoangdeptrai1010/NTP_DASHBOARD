package com.hospital.dashboard.department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "DMKHOAPHONGBV")
public class Department {

    @Id
    @Column(name = "IDKHOAPHONG")
    private Long id;

    @Column(name = "TENKHOAPHONG", nullable = false)
    private String name;
}
