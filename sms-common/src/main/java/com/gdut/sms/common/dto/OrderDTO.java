package com.gdut.sms.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.gdut.sms.common.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String orderNo;
    private BigDecimal orderAmount;
    private String serviceType;
    private String serviceContent;
    private Integer orderStatus;
    private LocalDateTime expectedTime;
    private LocalDateTime serviceTime;
    private String serviceAddress;
    private String remarks;

    private LocalDateTime createTime;
    private LocalDateTime auditTime;

    private String createdBy;
    private String auditedBy;
    private String customerNo;

    public OrderDTO(Order order) {
        orderNo = order.getOrderNo();
        orderAmount = order.getOrderAmount();
        serviceType = order.getServiceType();
        serviceContent = order.getServiceContent();
        orderStatus = order.getOrderStatus();
        expectedTime = order.getExpectedTime();
        serviceTime = order.getServiceTime();
        serviceAddress = order.getServiceAddress();
        remarks = order.getRemarks();

        createTime = order.getCreateTime();
        auditTime = order.getAuditTime();

        createdBy = order.getCreatedBy().getUsername();
        auditedBy = order.getAuditedBy() != null ? order.getAuditedBy().getUsername() : null;
        customerNo = order.getCustomer() != null ? order.getCustomer().getCustomerNo() : null;
    }
}
