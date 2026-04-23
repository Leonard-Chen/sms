package com.gdut.sms.common.entity;

import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import com.gdut.sms.common.dto.ScheduleDTO;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_schedule")
@Data
@NoArgsConstructor
public class ServiceSchedule {

    @Id
    private String scheduleNo;          //调度号

    @OneToOne
    @JoinColumn(name = "order_no", updatable = false)
    private Order order;                //订单

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_no", referencedColumnName = "employee_no", updatable = false)
    private Employee staff;             //服务人员

    private Byte scheduleStatus;        //状态 (1待执行/2执行中/3已完成/4异常)

    private LocalDateTime scheduleTime; //调度时间

    private LocalDateTime acceptTime;   //接受时间

    private LocalDateTime completeTime; //完成时间

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_by", updatable = false)
    private User scheduledBy;           //调度人

    private String remarks;             //备注

    public ServiceSchedule(ScheduleDTO dto) {
        scheduleNo = dto.getScheduleNo();
        scheduleStatus = dto.getScheduleStatus();
        scheduleTime = dto.getScheduleTime();
        acceptTime = dto.getAcceptTime();
        completeTime = dto.getCompleteTime();
        remarks = dto.getRemarks();
    }
}
