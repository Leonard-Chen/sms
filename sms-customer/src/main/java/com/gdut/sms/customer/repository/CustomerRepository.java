package com.gdut.sms.customer.repository;

import com.gdut.sms.common.entity.Customer;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByCustomerNo(String customerNo);

    void deleteByCustomerNo(String customerNo);
}
