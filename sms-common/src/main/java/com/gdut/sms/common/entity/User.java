package com.gdut.sms.common.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Collection;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_user")
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonTypeName("com.gdut.sms.common.entity.User")
public class User implements UserDetails {

    @Id
    private String username;            //用户名

    private String realName;            //真实姓名

    private String password;            //密码

    private String phone;               //手机号

    private String email;               //电子邮箱

    private Integer status = 1;         //状态 (0禁用/1启用)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_no", referencedColumnName = "dept_no")
    private Department dept;            //所在部门

    private LocalDateTime createTime;   //创建时间

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_code", referencedColumnName = "role_code")
    private Role role;                  //用户属于哪种角色

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role != null ? List.of(new SimpleGrantedAuthority(role.getRoleCode())) : List.of();
    }
}
