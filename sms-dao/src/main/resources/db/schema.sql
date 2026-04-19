
DROP TABLE IF EXISTS `oauth2_registered_client`;
DROP TABLE IF EXISTS `oauth2_authorization`;
DROP TABLE IF EXISTS `oauth2_authorization_consent`;
DROP TABLE IF EXISTS `sms_schedule`;
DROP TABLE IF EXISTS `sms_review`;
DROP TABLE IF EXISTS `sms_order`;
DROP TABLE IF EXISTS `sms_customer`;
DROP TABLE IF EXISTS `sms_employee`;
DROP TABLE IF EXISTS `sys_log`;
ALTER TABLE `sys_user`
    DROP CONSTRAINT `fk_user_dept`;
ALTER TABLE `sms_dept`
    DROP CONSTRAINT `fk_dept_user`;
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sms_dept`;

CREATE TABLE oauth2_registered_client
(
    `id`                            VARCHAR(100)                            NOT NULL,
    `client_id`                     VARCHAR(100)                            NOT NULL,
    `client_id_issued_at`           TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `client_secret`                 VARCHAR(200)  DEFAULT NULL,
    `client_secret_expires_at`      TIMESTAMP     DEFAULT NULL,
    `client_name`                   VARCHAR(200)                            NOT NULL,
    `client_authentication_methods` VARCHAR(1000)                           NOT NULL,
    `authorization_grant_types`     VARCHAR(1000)                           NOT NULL,
    `redirect_uris`                 VARCHAR(1000) DEFAULT NULL,
    `post_logout_redirect_uris`     VARCHAR(1000) DEFAULT NULL,
    `scopes`                        VARCHAR(1000)                           NOT NULL,
    `client_settings`               VARCHAR(2000)                           NOT NULL,
    `token_settings`                VARCHAR(2000)                           NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='客户端信息表';

CREATE TABLE oauth2_authorization
(
    `id`                            VARCHAR(100) NOT NULL,
    `registered_client_id`          VARCHAR(100) NOT NULL,
    `principal_name`                VARCHAR(200) NOT NULL,
    `authorization_grant_type`      VARCHAR(100) NOT NULL,
    `authorized_scopes`             VARCHAR(1000) DEFAULT NULL,
    `attributes`                    TEXT          DEFAULT NULL,
    `state`                         VARCHAR(500)  DEFAULT NULL,
    `authorization_code_value`      TEXT          DEFAULT NULL,
    `authorization_code_issued_at`  TIMESTAMP     DEFAULT NULL,
    `authorization_code_expires_at` TIMESTAMP     DEFAULT NULL,
    `authorization_code_metadata`   TEXT          DEFAULT NULL,
    `access_token_value`            TEXT          DEFAULT NULL,
    `access_token_issued_at`        TIMESTAMP     DEFAULT NULL,
    `access_token_expires_at`       TIMESTAMP     DEFAULT NULL,
    `access_token_metadata`         TEXT          DEFAULT NULL,
    `access_token_type`             VARCHAR(100)  DEFAULT NULL,
    `access_token_scopes`           VARCHAR(1000) DEFAULT NULL,
    `oidc_id_token_value`           TEXT          DEFAULT NULL,
    `oidc_id_token_issued_at`       TIMESTAMP     DEFAULT NULL,
    `oidc_id_token_expires_at`      TIMESTAMP     DEFAULT NULL,
    `oidc_id_token_metadata`        TEXT          DEFAULT NULL,
    `refresh_token_value`           TEXT          DEFAULT NULL,
    `refresh_token_issued_at`       TIMESTAMP     DEFAULT NULL,
    `refresh_token_expires_at`      TIMESTAMP     DEFAULT NULL,
    `refresh_token_metadata`        TEXT          DEFAULT NULL,
    `user_code_value`               TEXT          DEFAULT NULL,
    `user_code_issued_at`           TIMESTAMP     DEFAULT NULL,
    `user_code_expires_at`          TIMESTAMP     DEFAULT NULL,
    `user_code_metadata`            TEXT          DEFAULT NULL,
    `device_code_value`             TEXT          DEFAULT NULL,
    `device_code_issued_at`         TIMESTAMP     DEFAULT NULL,
    `device_code_expires_at`        TIMESTAMP     DEFAULT NULL,
    `device_code_metadata`          TEXT          DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='授权信息表 (用于存储token、授权码等)';

CREATE TABLE oauth2_authorization_consent
(
    `registered_client_id` VARCHAR(100)  NOT NULL,
    `principal_name`       VARCHAR(200)  NOT NULL,
    `authorities`          VARCHAR(1000) NOT NULL,
    PRIMARY KEY (`registered_client_id`, `principal_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='授权同意表 (用于存储用户的授权同意记录)';

CREATE TABLE `sys_role`
(
    `role_code`   VARCHAR(50) NOT NULL COMMENT '角色编码',
    `role_name`   VARCHAR(50) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    `status`      TINYINT(4)   DEFAULT 1 COMMENT '状态 (0禁用/1启用)',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`role_code`),
    UNIQUE KEY `uk_role_name` (`role_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色表';

CREATE TABLE `sys_user`
(
    `username`    CHAR(20)     NOT NULL COMMENT '用户名',
    `real_name`   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码 (加密存储)',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    `status`      TINYINT(4)   DEFAULT 1 COMMENT '状态 (0禁用/1启用)',
    `dept_no`     CHAR(20)     DEFAULT NULL COMMENT '所在部门编号',
    `role_code`   VARCHAR(50)  NOT NULL COMMENT '所属角色编码',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`username`),
    CONSTRAINT `fk_user_role` FOREIGN KEY (`role_code`) REFERENCES `sys_role` (`role_code`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `sms_dept`
(
    `dept_no`     CHAR(5)     NOT NULL COMMENT '部门编号',
    `dept_name`   VARCHAR(50) NOT NULL COMMENT '部门名',
    `status`      TINYINT(4) DEFAULT 1 COMMENT '状态 (0禁用/1启用)',
    `created_by`  CHAR(20)   DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`dept_no`),
    KEY `idx_dept_name` (`dept_name`),
    CONSTRAINT `chk_dept_no` CHECK (`dept_no` LIKE 'D%')
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='部门表';

ALTER TABLE `sms_dept`
    ADD CONSTRAINT `fk_dept_user` FOREIGN KEY (`created_by`) REFERENCES `sys_user` (`username`) ON DELETE SET NULL;
ALTER TABLE `sys_user`
    ADD CONSTRAINT `fk_user_dept` FOREIGN KEY (`dept_no`) REFERENCES `sms_dept` (`dept_no`) ON DELETE SET NULL;

CREATE TABLE `sys_log`
(
    `log_id`         VARCHAR(36)  NOT NULL DEFAULT (UUID()) COMMENT '日志编号',
    `username`       CHAR(20)              DEFAULT NULL COMMENT '用户ID',
    `module`         VARCHAR(50)  NOT NULL COMMENT '模块名',
    `operation_type` VARCHAR(50)  NOT NULL COMMENT '操作类型',
    `operation_desc` VARCHAR(50)  NOT NULL COMMENT '操作描述',
    `request_method` VARCHAR(20)  NOT NULL COMMENT '请求方法',
    `request_url`    VARCHAR(200) NOT NULL COMMENT '请求URL',
    `request_params` VARCHAR(200) NOT NULL COMMENT '请求参数',
    `ip_addr`        VARCHAR(50)  NOT NULL COMMENT '源IP地址',
    `time`           INT(20)      NOT NULL COMMENT '耗时',
    `log_time`       DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`log_id`),
    KEY `idx_username` (`username`),
    KEY `idx_module` (`module`),
    KEY `idx_operation_type` (`operation_type`),
    CONSTRAINT `fk_log_user` FOREIGN KEY (`username`) REFERENCES `sys_user` (`username`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='日志记录表';

CREATE TABLE `sms_customer`
(
    `customer_no`      CHAR(20)     NOT NULL COMMENT '客户编号',
    `customer_name`    VARCHAR(100) NOT NULL COMMENT '客户名称',
    `customer_type`    TINYINT(4)   DEFAULT 1 COMMENT '客户类型 (0企业/1个人)',
    `contact_person`   VARCHAR(50)  DEFAULT NULL COMMENT '联系人',
    `contact_phone`    VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    `address`          VARCHAR(200) DEFAULT NULL COMMENT '联系地址',
    `status`           TINYINT(4)   DEFAULT 1 COMMENT '状态 (0流失/1正常)',
    `follow_up_status` TINYINT(4)   DEFAULT NULL COMMENT '跟进状态 (1待跟进/2已跟进/3无需跟进)',
    `created_by`       CHAR(20)     DEFAULT NULL COMMENT '创建人',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`customer_no`),
    KEY `idx_customer_name` (`customer_name`),
    KEY `idx_created_by` (`created_by`),
    CONSTRAINT `chk_customer_no` CHECK (`customer_no` LIKE 'C%'),
    CONSTRAINT `fk_customer_created_by` FOREIGN KEY (`created_by`) REFERENCES `sys_user` (`username`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='客户表';

CREATE TABLE `sms_order`
(
    `order_no`        CHAR(20) NOT NULL COMMENT '订单编号',
    `customer_no`     CHAR(20) NOT NULL COMMENT '客户编号',
    `dept_no`         CHAR(20) NOT NULL COMMENT '部门编号',
    `service_type`    VARCHAR(50)    DEFAULT NULL COMMENT '服务类型',
    `service_content` VARCHAR(200)   DEFAULT NULL COMMENT '服务内容',
    `order_amount`    DECIMAL(10, 2) DEFAULT NULL COMMENT '订单金额',
    `order_status`    TINYINT(4)     DEFAULT NULL COMMENT '订单状态 (1待审核/2已生效/3服务中/4已完成/5已取消/6异常/7待分派/8待完成)',
    `expected_time`   DATETIME       DEFAULT NULL COMMENT '期望服务时间',
    `service_time`    DATETIME       DEFAULT NULL COMMENT '实际服务时间',
    `service_address` VARCHAR(200)   DEFAULT NULL COMMENT '服务地址',
    `remarks`         VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    `created_by`      CHAR(20)       DEFAULT NULL COMMENT '创建人',
    `create_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `audited_by`      CHAR(20)       DEFAULT NULL COMMENT '审核人',
    `audit_time`      DATETIME       DEFAULT NULL COMMENT '审核时间',
    PRIMARY KEY (`order_no`),
    KEY `idx_customer_no` (`customer_no`),
    KEY `idx_created_by` (`created_by`),
    KEY `idx_audit_by` (`audited_by`),
    CONSTRAINT `chk_order_no` CHECK (`order_no` LIKE 'O%'),
    CONSTRAINT `fk_order_customer` FOREIGN KEY (`customer_no`) REFERENCES `sms_customer` (`customer_no`),
    CONSTRAINT `fk_order_created_by` FOREIGN KEY (`created_by`) REFERENCES `sys_user` (`username`) ON DELETE SET NULL,
    CONSTRAINT `fk_order_audit_by` FOREIGN KEY (`audited_by`) REFERENCES `sys_user` (`username`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单表';

CREATE TABLE `sms_employee`
(
    `employee_no`   CHAR(20)    NOT NULL COMMENT '员工编号',
    `employee_name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
    `dept_no`       CHAR(20)     DEFAULT NULL COMMENT '部门ID',
    `position`      VARCHAR(50)  DEFAULT NULL COMMENT '职位',
    `skills`        VARCHAR(200) DEFAULT NULL COMMENT '技能标签',
    `work_status`   TINYINT(4)   DEFAULT NULL COMMENT '工作状态 (1空闲/2忙碌/3休假)',
    `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    `hired_date`    DATE         DEFAULT NULL COMMENT '入职日期',
    `created_by`    CHAR(20)     DEFAULT NULL COMMENT '创建人',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`employee_no`),
    KEY (`employee_name`),
    CONSTRAINT `chk_employee_no` CHECK (`employee_no` LIKE 'E%'),
    CONSTRAINT `fk_employee_dept` FOREIGN KEY (`dept_no`) REFERENCES sms_dept (`dept_no`) ON DELETE SET NULL,
    CONSTRAINT `fk_employee_created_by` FOREIGN KEY (`created_by`) REFERENCES `sys_user` (`username`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='员工表';

CREATE TABLE `sms_schedule`
(
    `schedule_no`     CHAR(20) NOT NULL COMMENT '调度号',
    `order_no`        CHAR(20) NOT NULL COMMENT '订单编号',
    `staff_no`        CHAR(20) NOT NULL COMMENT '服务人员编号',
    `schedule_status` TINYINT(4)   DEFAULT NULL COMMENT '状态 (1待执行/2执行中/3已完成/4异常)',
    `schedule_time`   DATETIME     DEFAULT NULL COMMENT '调度时间',
    `accept_time`     DATETIME     DEFAULT NULL COMMENT '接受时间',
    `complete_time`   DATETIME     DEFAULT NULL COMMENT '完成时间',
    `scheduled_by`    CHAR(20)     DEFAULT NULL COMMENT '调度人',
    `remarks`         VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`schedule_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_staff_no` (`staff_no`),
    KEY `idx_schedule_time` (`schedule_time`),
    CONSTRAINT `chk_schedule_no` CHECK (`schedule_no` LIKE 'S%'),
    CONSTRAINT `fk_schedule_order` FOREIGN KEY (`order_no`) REFERENCES `sms_order` (`order_no`),
    CONSTRAINT `fk_schedule_staff` FOREIGN KEY (`staff_no`) REFERENCES `sms_employee` (`employee_no`),
    CONSTRAINT `fk_schedule_scheduled_by` FOREIGN KEY (`scheduled_by`) REFERENCES `sys_user` (`username`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='服务调度表';

CREATE TABLE `sms_review`
(
    `review_no`   CHAR(20) NOT NULL COMMENT '评价编号',
    `order_no`    CHAR(20) NOT NULL COMMENT '订单编号',
    `customer_no` CHAR(20) NOT NULL COMMENT '客户编号',
    `staff_no`    CHAR(20) NOT NULL COMMENT '服务人员编号',
    `score`       TINYINT(4)   DEFAULT NULL COMMENT '评分 (1-5)',
    `content`     VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    `review_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    PRIMARY KEY (`review_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_customer_no` (`customer_no`),
    KEY `idx_staff_no` (`staff_no`),
    CONSTRAINT `chk_review_no` CHECK (`review_no` LIKE 'R%'),
    CONSTRAINT `fk_review_order` FOREIGN KEY (`order_no`) REFERENCES `sms_order` (`order_no`),
    CONSTRAINT `fk_review_customer` FOREIGN KEY (`customer_no`) REFERENCES `sms_customer` (`customer_no`),
    CONSTRAINT `fk_review_staff` FOREIGN KEY (`staff_no`) REFERENCES `sms_employee` (`employee_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='客户评价表';
