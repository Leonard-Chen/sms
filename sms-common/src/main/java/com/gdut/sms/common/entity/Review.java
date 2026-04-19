package com.gdut.sms.common.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_review")
@Data
public class Review {

    @Id
    private String reviewNo;            //评价编号

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_no")
    private Order order;                //订单

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_no")
    private Customer customer;          //顾客

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_no")
    private Employee staff;             //给哪位工作人员评价

    private Integer score;              //评分 (1-5)

    private String content;             //评价内容

    private LocalDateTime reviewTime;   //评价时间
}
