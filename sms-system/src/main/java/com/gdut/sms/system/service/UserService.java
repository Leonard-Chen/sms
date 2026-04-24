package com.gdut.sms.system.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.entity.User;
import com.gdut.sms.common.entity.Role;
import com.gdut.sms.common.dto.UserDTO;
import com.gdut.sms.common.entity.Department;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CachePut;
import com.gdut.sms.system.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

/**
 * 用户认证及管理核心服务
 *
 * @author ckx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws RuntimeException {
        User user = userRepository.findByUsernameWithDeptAndRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        return user;
    }

    @Cacheable(value = "user", key = "#username", unless = "#result == null")
    public UserDTO get(String username) throws UsernameNotFoundException {
        return new UserDTO(userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username)));
    }

    @Transactional
    @Cacheable(value = "user_list", unless = "#result == null || #result.empty")
    public List<UserDTO> list() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .toList();
    }

    @Transactional
    @Caching(
            evict = @CacheEvict(value = "user_list", allEntries = true)
    )
    public UserDTO create(UserDTO dto) throws RuntimeException {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("用户已存在");
        }

        return save(dto);
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "user", key = "#dto.username"),
            evict = @CacheEvict(value = "user_list", allEntries = true)
    )
    public UserDTO update(UserDTO dto) throws RuntimeException {
        return save(dto);
    }

    public UserDTO save(UserDTO dto) throws RuntimeException {
        if (dto.getUsername() == null || dto.getUsername().isEmpty()
                || dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setRealName(dto.getRealName());
        user.setPassword(dto.getPassword());
        user.setStatus(dto.getStatus());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        Department dept = null;
        if (dto.getDeptNo() != null && !dto.getDeptNo().isEmpty()) {
            dept = new Department();
            dept.setDeptNo(dto.getDeptNo());
        }
        user.setDept(dept);

        Role role = null;
        if (dto.getRoleCode() != null && !dto.getRoleCode().isEmpty()) {
            role = new Role();
            role.setRoleCode(dto.getRoleCode());
        }
        user.setRole(role);

        return new UserDTO(userRepository.save(user));
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "user", key = "#username"),
                    @CacheEvict(value = "user_list", allEntries = true)
            }
    )
    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }
}
