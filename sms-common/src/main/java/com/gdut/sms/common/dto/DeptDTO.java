package com.gdut.sms.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.gdut.sms.common.entity.Department;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptDTO {
    private String deptNo;
    private String deptName;

    private String createdBy;
    private LocalDateTime createTime;

    public DeptDTO(Department dept) {
        deptNo = dept.getDeptNo();
        deptName = dept.getDeptName();
        createdBy = dept.getCreatedBy().getUsername();
        createTime = dept.getCreateTime();
    }
}
