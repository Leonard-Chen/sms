package com.gdut.sms.employee.service;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.entity.User;
import com.gdut.sms.common.entity.Employee;
import com.gdut.sms.common.dto.EmployeeDTO;
import com.gdut.sms.common.utils.RandomUUID;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import com.gdut.sms.employee.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 后端员工管理核心业务
 * @author ckx
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Cacheable(value = "employee_list", unless = "#result == null || #result.empty")
    public List<EmployeeDTO> list() {
        return employeeRepository.findAll().stream()
                .map(EmployeeDTO::new)
                .toList();
    }

    @Cacheable(value = "employee", key = "#no", unless = "#result == null")
    public EmployeeDTO get(String no) {
        return new EmployeeDTO(employeeRepository.findByEmployeeNo(no)
                .orElseThrow(() -> new RuntimeException("员工不存在")));
    }

    @Transactional
    @CacheEvict(value = "employee_list", allEntries = true)
    public EmployeeDTO create(EmployeeDTO dto, String username) {
        String no;
        do {
            no = "E" + RandomUUID.generate(19, RandomUUID.CharType.DIGIT);
        } while (employeeRepository.findByEmployeeNo(no).isPresent());

        User user = new User();
        user.setUsername(username);

        Employee employee = new Employee(dto);
        employee.setEmployeeNo(no);
        employee.setCreatedBy(user);
        employee.setCreateTime(LocalDateTime.now());

        return new EmployeeDTO(employeeRepository.save(employee));
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "employee", key = "#dto.employeeNo"),
            evict = @CacheEvict(value = "employee_list", allEntries = true)
    )
    public EmployeeDTO update(EmployeeDTO dto) {
        employeeRepository.findByEmployeeNo(dto.getEmployeeNo())
                .orElseThrow(() -> new RuntimeException("员工不存在"));

        return new EmployeeDTO(employeeRepository.save(new Employee(dto)));
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "employee", key = "#no"),
            evict = @CacheEvict(value = "employee_list", allEntries = true)
    )
    public EmployeeDTO updateWorkStatus(String no, Integer status) {
        Employee employee = employeeRepository.findByEmployeeNo(no)
                .orElseThrow(() -> new RuntimeException("员工不存在"));

        employee.setWorkStatus(status);

        return new EmployeeDTO(employeeRepository.save(employee));
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "employee", key = "#no"),
            evict = @CacheEvict(value = "employee_list", allEntries = true)
    )
    public void delete(String no) {
        employeeRepository.deleteByEmployeeNo(no);
    }
}
