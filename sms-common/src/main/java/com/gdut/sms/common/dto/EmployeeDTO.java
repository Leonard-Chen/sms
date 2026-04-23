package com.gdut.sms.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.gdut.sms.common.entity.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private String employeeNo;
    private String employeeName;
    private String deptNo;
    private String position;
    private String skills;
    private Integer workStatus;
    private String phone;
    private LocalDate hiredDate;
    private String createdBy;
    private LocalDateTime createTime;

    public EmployeeDTO(Employee employee) {
        employeeNo = employee.getEmployeeNo();
        employeeName = employee.getEmployeeName();
        deptNo = employee.getDept().getDeptNo();
        position = employee.getPosition();
        skills = employee.getSkills();
        workStatus = employee.getWorkStatus();
        phone = employee.getPhone();
        hiredDate = employee.getHiredDate();
        createdBy = employee.getCreatedBy().getUsername();
        createTime = employee.getCreateTime();
    }
}
