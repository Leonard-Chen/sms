package com.gdut.sms.common.entity;

import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import com.gdut.sms.common.dto.OrderDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sms_order")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @Column(name = "order_no")
    private String orderNo;             //订单编号

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_no")
    private Customer customer;          //下单的客户

    @ManyToOne
    @JoinColumn(name = "dept_no", referencedColumnName = "dept_no")
    private Department dept;      //订单所在部门

    private String serviceType;         //服务类型

    private String serviceContent;      //服务内容

    @Column(name = "order_amount", precision = 10, scale = 2)
    private BigDecimal orderAmount;     //订单金额

    private Integer orderStatus;        //订单状态 (1待审核/2已生效/3服务中/4已完成/5已取消/6异常/7待分派/8待完成)

    private LocalDateTime expectedTime; //期望服务时间

    private LocalDateTime serviceTime;  //实际服务时间

    private String serviceAddress;      //服务地址

    private String remarks;             //备注

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "username")
    private User createdBy;             //创建人

    private LocalDateTime createTime;   //创建时间

    @ManyToOne
    @JoinColumn(name = "audited_by", referencedColumnName = "username")
    private User auditedBy;             //审核人

    private LocalDateTime auditTime;    //审核时间

    public Order(OrderDTO dto) {
        copyFrom(dto);
    }

    public void copyFrom(OrderDTO dto) {
        orderNo = dto.getOrderNo();
        serviceType = dto.getServiceType();
        serviceContent = dto.getServiceContent();
        orderAmount = dto.getOrderAmount();
        orderStatus = dto.getOrderStatus();
        expectedTime = dto.getExpectedTime();
        serviceTime = dto.getServiceTime();
        serviceAddress = dto.getServiceAddress();
        remarks = dto.getRemarks();
    }
}
