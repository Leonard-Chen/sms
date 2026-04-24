package com.gdut.sms.common.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_role")
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonTypeName("com.gdut.sms.common.entity.Role")
public class Role {

    @Id
    @Column(name = "role_code")
    private String roleCode;                //角色编码

    private String roleName;                //角色名称

    private String description;             //描述

    private Integer status = 1;             //状态 (0禁用/1启用)

    @Column(name = "create_time", insertable = false, updatable = false)
    private LocalDateTime createTime;       //创建时间
}
