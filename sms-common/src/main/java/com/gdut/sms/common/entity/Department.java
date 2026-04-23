package com.gdut.sms.common.entity;

import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import com.gdut.sms.common.dto.DeptDTO;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_dept")
@Data
@NoArgsConstructor
public class Department {

    @Id
    @Column(name = "dept_no")
    private String deptNo;              //部门编号

    private String deptName;            //部门名称

    private Integer status = 1;         //状态 (0禁用/1启用)

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "username")
    private User createdBy;             //创建人

    private LocalDateTime createTime;   //创建时间

    public Department(DeptDTO dto) {
        copyFrom(dto);
    }

    public void copyFrom(DeptDTO dto) {
        deptNo = dto.getDeptNo();
        deptName = dto.getDeptName();
        status = dto.getStatus();
    }

}
