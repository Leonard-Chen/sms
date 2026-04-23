package com.gdut.sms.customer.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.entity.User;
import com.gdut.sms.common.entity.Customer;
import com.gdut.sms.common.dto.CustomerDTO;
import com.gdut.sms.common.utils.RandomUUID;
import org.springframework.stereotype.Service;
import com.gdut.sms.common.utils.DateTimeUtils;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import com.gdut.sms.customer.repository.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 后端客户管理核心业务
 *
 * @author ckx
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Cacheable(value = "customer_list", unless = "#result == null || #result.empty")
    public List<CustomerDTO> list() {
        return customerRepository.findAll().stream()
                .map(CustomerDTO::new)
                .toList();
    }

    @Cacheable(value = "customer_count", key = "#year != null ? #year : 'all'", unless = "#result == null")
    public Long count(@Nullable Integer year) {
        return year == null ? customerRepository.count()
                : customerRepository.countByCreateTimeBetween(
                DateTimeUtils.firstTimeOfTheYear(year),
                DateTimeUtils.lastTimeOfTheYear(year)
        );
    }

    @Cacheable(value = "customer", key = "#customerNo", unless = "#result == null")
    public CustomerDTO get(String customerNo) {
        return new CustomerDTO(customerRepository.findByCustomerNo(customerNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"))
        );
    }

    @Transactional
    @CacheEvict(value = "customer_list", allEntries = true)
    public CustomerDTO create(CustomerDTO dto, String username) {
        String no;
        do {
            no = "C" + RandomUUID.generate(19, RandomUUID.CharType.DIGIT);
        } while (customerRepository.findByCustomerNo(no).isPresent());

        User user = new User();
        user.setUsername(username);

        Customer customer = new Customer(dto);
        customer.setFollowUpStatus(1);
        customer.setCustomerNo(no);
        customer.setCreatedBy(user);

        return new CustomerDTO(customerRepository.save(customer));
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "customer", key = "#dto.customerNo"),
            evict = @CacheEvict(value = "customer_list", allEntries = true)
    )
    public CustomerDTO update(CustomerDTO dto) {
        Customer customer = customerRepository.findByCustomerNo(dto.getCustomerNo())
                .orElseThrow(() -> new RuntimeException("客户不存在"));

        return new CustomerDTO(customerRepository.save(new Customer(dto)));
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "customer", key = "#customerNo"),
                    @CacheEvict(value = "customer_list", allEntries = true)
            }
    )
    public void delete(String customerNo) {
        customerRepository.deleteByCustomerNo(customerNo);
    }
}
