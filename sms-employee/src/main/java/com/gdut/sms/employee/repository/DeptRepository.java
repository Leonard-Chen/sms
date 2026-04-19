package com.gdut.sms.employee.repository;

import com.gdut.sms.common.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeptRepository extends JpaRepository<Department, String> {

    Optional<Department> findByDeptNo(String deptNo);

    void deleteByDeptNo(String deptNo);
}
