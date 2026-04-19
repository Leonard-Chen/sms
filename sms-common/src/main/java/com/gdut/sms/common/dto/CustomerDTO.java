package com.gdut.sms.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.gdut.sms.common.entity.Customer;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private String customerNo;
    private String customerName;
    private Integer customerType;
    private String contactPerson;
    private String contactPhone;
    private String address;
    private Integer followUpStatus;
    private LocalDateTime createTime;

    private String createdBy;

    public CustomerDTO(Customer customer) {
        customerNo = customer.getCustomerNo();
        customerName = customer.getCustomerName();
        customerType = customer.getCustomerType();
        contactPerson = customer.getContactPerson();
        contactPhone = customer.getContactPhone();
        address = customer.getAddress();
        followUpStatus = customer.getFollowUpStatus();
        createTime = customer.getCreateTime();

        createdBy = customer.getCreatedBy() != null ? customer.getCreatedBy().getUsername() : null;
    }
}
