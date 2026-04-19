#测试数据

INSERT INTO `sms_dept` (`dept_no`, `dept_name`, `status`, `create_time`)
VALUES ('D8639', '技术部', 1, NOW()),
       ('D4746', '销售部', 1, NOW()),
       ('D0305', 'xx部', 1, NOW()),
       ('D9535', 'yy部', 1, NOW()),
       ('D1001', '人力资源部', 1, NOW()),
       ('D1002', '财务部', 1, NOW()),
       ('D2001', '市场部', 0, NOW());

INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `status`, `create_time`)
VALUES ('系统管理员', 'ADMIN', '拥有所有权限', 1, NOW()),
       ('普通业务人员', 'USER', '仅拥有日常业务操作权限', 1, NOW()),
       ('部门经理', 'MANAGER', '管理本部门事务及审批', 1, NOW()),
       ('企业负责人', 'OWNER', '企业最高权限，可管理所有业务', 1, NOW());

INSERT INTO `sys_user` (`username`, `real_name`, `password`, `phone`, `email`, `status`, `dept_no`, `role_code`,
                        `create_time`)
VALUES ('monika', '莫妮卡', '$2a$10$JnB2jTThnc3L2gV0kSB5tuCl267cu7zeHP.KIlwq8lkve.CpFYJqi',#justmonika
        '13800000000', 'monika@ddlc.com', 1, NULL, 'ADMIN', NOW()),
       ('hikari', '光', '$2a$10$RH2zlBQsKt4JdD8yFlwX8ejG9Sm7UI/vs6BqzL89dydkmLnGJjkra',#testify
        '13912345678', 'hikari@arcaea.com', 1, 'D0305', 'USER', NOW()),
       ('tairitsu', '对立', '$2a$10$zukh37BOyfzcAvzP5kjKNOtB43ZKVOUWarKreaYHMc.wgiTI.C8..',#tempestissimo
        '15987654321', 'tairitsu@arcaea.com', 1, 'D9535', 'USER', NOW()),
       ('nitro_lowiro', 'Vernon Tan', '$2a$10$1SKRWEjt9awWLDTcP2hcIel3OzjMQ7rNOVrMwJewef6t9uf45ye/K',#vernon
        '13912345678', 'nitro@lowiro.com', 1, 'D4746', 'MANAGER', NOW()),
       ('guy_lowiro', 'Anton Prydatko', '$2a$10$3geOY.195oTEvPD/EvHmeu5COuNqzr9OV3xFwi4KbcXdabP4YOe.C',#anton
        '15987654321', 'guy@lowiro.com', 1, NULL, 'OWNER', NOW()),
       ('hakurei_reimu', '灵梦', '$2a$10$Q7VXxB1FeWL4ylDRGBrq0e3rssc0DJ.qCQVhEgG8WV18l/57LexCa', '13612345678',
        'reimu@gensokyo.com', 1, 'D1002', 'USER', NOW()),
       ('izayoi_sakuya', '咲夜', '$2a$10$R0wxvY4Za.4oqfj2JQcHt.l9ldszmfa7SjutxJZlzYiBzj5O79GMq', '15012345678',
        'sakuya@gensokyo.com', 1, NULL, 'ADMIN', NOW()),
       ('kirisame_marisa', '魔理沙', '$2a$10$M53cSs7oHwrGrXVllsybKuv0KGn8G3rACfCoB.hhe7Nwi/1wj57MC', '13712345678',
        'marisa@gensokyo.com', 1, 'D2001', 'USER', NOW()),
       ('kochiya_sanae', '早苗', '$2a$10$qJQBWdyphENXTGXtWNhKt.BX6Byxjzuc8CAD5wTuIKgJw3n.XvuhO', '18888888888',
        'sanae@gensokyo.com', 1, 'D1001', 'MANAGER', NOW()),
       ('komeiji_koishi', '恋、无意识', '$2a$10$kP9xqM02hJOB82ViWEZHAuHl6OzcRV7w1qgEET8Il5xOVxmt5lhg6', '13000000000',
        'koishi@gensokyo.com', 0, NULL, 'OWNER', NOW());
;

UPDATE `sms_dept`
SET created_by = 'monika';

INSERT INTO `sms_employee` (`employee_no`, `employee_name`, `dept_no`, `position`, `skills`, `work_status`,
                            `phone`, `hired_date`, `created_by`, `create_time`)
VALUES ('E4145242483098614966', '王师傅', 'D8639', '高级工程师', '家电维修,管道疏通', 1, '13811112222',
        '2023-01-10', 'guy_lowiro',
        NOW()),
       ('E6878605763125209399', '李师傅', 'D8639', '中级工程师', '空调清洗,水电维修', 2, '13811113333',
        '2023-03-15', 'guy_lowiro',
        NOW()),
       ('E3498996111708977940', '张顾问', 'D4746', '客户经理', '业务咨询,合同签订', 1, '13811114444',
        '2023-02-20', 'guy_lowiro', NOW()),
       ('E1111111111111111111', '赵工', 'D8639', '初级工程师', '家电维修', 3, '13822223333', '2024-01-15',
        'izayoi_sakuya', NOW()),
       ('E2222222222222222222', '钱师傅', 'D8639', '高级技师', '中央空调维修,新风系统', 2, '13822224444', '2022-08-10',
        'izayoi_sakuya', NOW()),
       ('E3333333333333333333', '孙经理', 'D4746', '销售总监', '大客户谈判,团队管理', 1, '13822225555', '2023-05-20',
        'kirisame_marisa', NOW()),
       ('E4444444444444444444', '周专员', 'D1001', 'HRBP', '招聘,员工关系', 1, '13822226666', '2024-03-01',
        'izayoi_sakuya', NOW()),
       ('E5555555555555555555', '吴师傅', 'D8639', '中级工程师', '水电维修', 3, '13900001111', '2021-09-01',
        'guy_lowiro', NOW());

INSERT INTO `sms_customer` (`customer_no`, `customer_name`, `customer_type`, `contact_person`,
                            `contact_phone`, `address`, `status`, `follow_up_status`, `created_by`, `create_time`)
VALUES ('C6023504097733215462', '张三', 1, '张三', '13912345678', '北京市朝阳区xxx', 1, 1, 'hikari', NOW()),
       ('C6153075251555350697', 'xx科技有限公司', 0, '李四', '010-12345678', '上海市浦东新区xxx', 1, 2, 'hikari',
        NOW()),
       ('C6687824358208076098', '王五', 1, '王五', '15987654321', '广州市天河区xxx', 1, 3, 'tairitsu', NOW()),
       ('C1111111111111111111', '赵六', 1, '赵六', '13611112222', '深圳市南山区科技园', 1, 2, 'hakurei_reimu', NOW()),
       ('C2222222222222222222', '蓝海科技有限公司', 0, '周经理', '0755-87654321', '深圳市福田区xx大厦', 1, 1,
        'kirisame_marisa', NOW()),
       ('C3333333333333333333', '陈七', 1, '陈七', '15812345678', '成都市高新区xxx', 1, 2, 'tairitsu', NOW()),
       ('C4444444444444444444', '账号已注销', 1, '棍母', '10000000000', '未知', 0, 3, 'hikari', NOW());


INSERT INTO `sms_order` (`order_no`, `customer_no`, `dept_no`, `service_type`, `service_content`,
                         `order_amount`, `order_status`, `expected_time`, `service_time`, `service_address`, `remarks`,
                         `created_by`, `create_time`, `audited_by`, `audit_time`)
VALUES ('O6536240472192992573', 'C6023504097733215462', 'D8639', '家电维修', '冰箱不制冷维修', 200.00, 3,
        '2025-03-20 14:00:00',
        '2025-03-20 15:30:00',
        '北京市朝阳区xxx', '用户反馈冰箱不制冷', 'hikari', NOW(), 'nitro_lowiro', NOW()),
       ('O7430733566767721889', 'C6153075251555350697', 'D4746', '企业服务', '办公网络布线', 5000.00, 2,
        '2025-03-25 09:00:00', NULL,
        '上海市浦东新区xxx',
        '需提前联系物业', 'hikari', NOW(), 'nitro_lowiro', NOW()),
       ('O9804360586197350429', 'C6687824358208076098', 'D8639', '空调清洗', '家用中央空调清洗', 800.00,
        1, '2025-04-01 10:00:00', NULL,
        '广州市天河区xxx',
        NULL, 'tairitsu', NOW(), NULL, NULL),
       ('O1111111111111111111', 'C1111111111111111111', 'D8639', '家电维修', '洗衣机漏水维修', 150.00, 1,
        '2026-04-18 09:00:00', NULL, '深圳市南山区科技园', '客户要求下午', 'hakurei_reimu', '2026-04-16 20:58:09', NULL,
        NULL),
       ('O2222222222222222222', 'C2222222222222222222', 'D8639', '办公设备维护', '打印机/复印机检修', 800.00, 7,
        '2026-04-20 14:00:00', NULL, '深圳市福田区xx大厦', NULL, 'kirisame_marisa', '2026-04-16 20:58:09',
        'izayoi_sakuya', '2026-04-16 20:58:09'),
       ('O3333333333333333333', 'C3333333333333333333', 'D8639', '空调清洗', '柜机空调深度清洗', 300.00, 4,
        '2026-03-28 11:00:00', '2026-03-28 12:30:00', '成都市高新区xxx', '服务满意', 'tairitsu', '2026-03-27 09:00:00',
        'nitro_lowiro', '2026-03-27 10:00:00'),
       ('O4444444444444444444', 'C6023504097733215462', 'D4746', '企业服务', '办公网络布线', 5000.00, 5,
        '2026-04-05 10:00:00', NULL, '北京市朝阳区xxx', '客户主动取消', 'hikari', '2026-04-01 08:00:00', 'nitro_lowiro',
        '2026-04-01 09:00:00'),
       ('O5555555555555555555', 'C1111111111111111111', 'D8639', '管道疏通', '厨房下水道疏通', 120.00, 3,
        '2026-04-16 16:00:00', '2026-04-16 16:20:00', '深圳市南山区科技园', '紧急处理', 'hakurei_reimu',
        '2026-04-16 20:58:09', NULL, NULL),
       ('O6666666666666666666', 'C6687824358208076098', 'D8639', '家电维修', '电视屏幕更换', 950.00, 6,
        '2026-04-10 13:00:00', '2026-04-10 13:10:00', '广州市天河区xxx', '配件不匹配，待重约', 'tairitsu',
        '2026-04-09 10:00:00', 'nitro_lowiro', '2026-04-10 09:30:00');
;

INSERT INTO `sms_schedule` (`schedule_no`, `order_no`, `staff_no`, `schedule_status`,
                            `schedule_time`, `accept_time`, `complete_time`, `scheduled_by`, `remarks`)
VALUES ('S4926492798429431683', 'O6536240472192992573', 'E4145242483098614966', 3, '2025-03-19 10:00:00',
        '2025-03-19 10:05:00', '2025-03-20 16:00:00',
        'nitro_lowiro',
        '已按时完成'),
       ('S7312825346111514017', 'O7430733566767721889', 'E3498996111708977940', 1, '2025-03-23 09:00:00', NULL, NULL,
        'nitro_lowiro', '待执行'),
       ('S5500168184724342849', 'O9804360586197350429', 'E6878605763125209399', 1, '2025-03-30 14:00:00', NULL, NULL,
        'nitro_lowiro', '等待确认');

INSERT INTO `sms_review` (`review_no`, `order_no`, `customer_no`, `staff_no`, `score`, `content`,
                          `review_time`)
VALUES ('R2833460691803607518', 'O6536240472192992573', 'C6023504097733215462', 'E4145242483098614966', 5,
        '技术专业，服务态度很好，非常满意！', NOW()),
       ('R8946075155697667741', 'O7430733566767721889', 'C6153075251555350697', 'E3498996111708977940', 4,
        '沟通顺畅，但希望能提前告知具体时间。', NOW());
