package com.gdut.sms.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.gdut.sms.common.entity.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;

    private String deptNo;
    private String roleCode;

    public UserDTO(User user) {
        username = user.getUsername();
        realName = user.getRealName();
        email = user.getEmail();
        phone = user.getPhone();
        status = user.getStatus();
        createTime = user.getCreateTime();
        deptNo = user.getDept() != null ? user.getDept().getDeptNo() : null;
        roleCode = user.getRole() != null ? user.getRole().getRoleCode() : null;
    }
}
