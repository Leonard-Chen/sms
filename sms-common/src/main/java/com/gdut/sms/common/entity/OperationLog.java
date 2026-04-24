package com.gdut.sms.common.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_log")
@Data
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String logId;             //日志ID

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    private User user;              //用户

    private String module;          //所在模块

    private String operationType;   //操作类型

    private String operationDesc;   //操作描述

    private String requestMethod;   //请求方法

    private String requestUrl;      //请求资源URL

    private String requestParams;   //请求参数

    @Column(name = "ip_addr")
    private String ipAddress;       //源IP地址

    private Long time;              //耗时 (单位ms)

    @Column(name = "log_time", insertable = false, updatable = false)
    private LocalDateTime logTime;  //日志记录时间
}
