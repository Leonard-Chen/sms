package com.gdut.sms.common.entity;

import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import com.gdut.sms.common.dto.CustomerDTO;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_customer")
@Data
@NoArgsConstructor
public class Customer {

    @Id
    private String customerNo;          //客户编号

    private String customerName;        //客户姓名

    private Integer customerType = 1;   //客户类型 (0企业/1个人)

    private String contactPerson;       //联系人姓名

    private String contactPhone;        //联系电话

    private String address;             //联系地址

    private Integer status = 1;         //状态 (0流失/1正常)

    private Integer followUpStatus;     //跟进状态 (1待跟进/2已跟进/3无需跟进)

    @ManyToOne
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdBy;             //创建人

    private LocalDateTime createTime;   //创建时间

    public Customer(CustomerDTO dto) {
        customerNo = dto.getCustomerNo();
        customerName = dto.getCustomerName();
        customerType = dto.getCustomerType();
        contactPerson = dto.getContactPerson();
        contactPhone = dto.getContactPhone();
        address = dto.getAddress();
        followUpStatus = dto.getFollowUpStatus();
    }
}
