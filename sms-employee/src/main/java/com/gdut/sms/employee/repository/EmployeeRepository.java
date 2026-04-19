package com.gdut.sms.employee.repository;

import com.gdut.sms.common.entity.Employee;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmployeeNo(String employeeNo);

    void deleteByEmployeeNo(String employeeNo);

}
