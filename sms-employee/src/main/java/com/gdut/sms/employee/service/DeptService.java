package com.gdut.sms.employee.service;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.entity.User;
import com.gdut.sms.common.dto.DeptDTO;
import com.gdut.sms.common.utils.RandomUUID;
import com.gdut.sms.common.entity.Department;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import com.gdut.sms.employee.repository.DeptRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 后端部门管理核心业务
 *
 * @author ckx
 */
@Service
@RequiredArgsConstructor
public class DeptService {

    private final DeptRepository deptRepository;

    @Cacheable(value = "dept_list", unless = "#result == null || #result.empty")
    public List<Department> list() {
        return deptRepository.findAll();
    }

    @Cacheable(value = "dept", key = "#no", unless = "#result == null")
    public Department get(String no) {
        return deptRepository.findByDeptNo(no).orElse(null);
    }

    @Transactional
    public DeptDTO create(DeptDTO dto, String username) {
        String no;
        do {
            no = "D" + RandomUUID.generate(4, RandomUUID.CharType.DIGIT);
        } while (deptRepository.findByDeptNo(no).isPresent());

        User user = new User();
        user.setUsername(username);

        Department dept = new Department(dto);
        dept.setDeptNo(no);
        dept.setCreatedBy(user);
        dept.setCreateTime(LocalDateTime.now());

        return new DeptDTO(deptRepository.save(dept));
    }

    @Transactional
    @CachePut(value = "dept", key = "#dto.deptNo")
    public DeptDTO update(DeptDTO dto) {
        Department dept = deptRepository.findByDeptNo(dto.getDeptNo())
                .orElseThrow(() -> new RuntimeException("部门不存在"));

        dept.copyFrom(dto);

        return new DeptDTO(deptRepository.save(dept));
    }

    @Transactional
    @CacheEvict(value = "dept", key = "#no")
    public void delete(String no) {
        deptRepository.deleteByDeptNo(no);
    }

}
