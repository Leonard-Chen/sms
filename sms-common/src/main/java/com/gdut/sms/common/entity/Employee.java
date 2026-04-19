package com.gdut.sms.common.entity;

import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import com.gdut.sms.common.dto.EmployeeDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sms_employee")
@Data
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_no")
    private String employeeNo;          //员工编号

    private String employeeName;        //员工姓名

    @ManyToOne
    @JoinColumn(name = "dept_no", referencedColumnName = "dept_no")
    private Department dept;            //所在部门

    private String position;            //职位

    private String skills;              //技能标签

    private Integer workStatus;         //工作状态 (1空闲/2忙碌/3休假)

    private String phone;               //联系电话

    private LocalDate hiredDate;        //入职日期

    @ManyToOne
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdBy;             //创建人

    private LocalDateTime createTime;   //创建时间

    public Employee(EmployeeDTO dto) {
        employeeNo = dto.getEmployeeNo();
        employeeName = dto.getEmployeeName();
        position = dto.getPosition();
        skills = dto.getSkills();
        workStatus = dto.getWorkStatus();
        phone = dto.getPhone();
        hiredDate = dto.getHiredDate();
    }
}
