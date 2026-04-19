package com.gdut.sms.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.gdut.sms.common.entity.ServiceSchedule;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private String scheduleNo;
    private String orderNo;
    private String staffNo;
    private Byte scheduleStatus;
    private LocalDateTime scheduleTime;
    private LocalDateTime acceptTime;
    private LocalDateTime completeTime;
    private String remarks;

    public ScheduleDTO(ServiceSchedule schedule) {
        scheduleNo = schedule.getScheduleNo();
        orderNo = schedule.getOrder().getOrderNo();
        staffNo = schedule.getStaff().getEmployeeNo();
        scheduleStatus = schedule.getScheduleStatus();
        scheduleTime = schedule.getScheduleTime();
        acceptTime = schedule.getAcceptTime();
        completeTime = schedule.getCompleteTime();
        remarks = schedule.getRemarks();
    }
}
