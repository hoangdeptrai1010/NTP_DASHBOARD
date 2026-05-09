package com.hospital.dashboard.department;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @GetMapping("/api/departments")
    public List<DepartmentResponse> getDepartments() {
        return departmentRepository.findAll().stream()
            .map(department -> new DepartmentResponse(department.getId(), department.getName()))
            .toList();
    }
}
