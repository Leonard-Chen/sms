package com.gdut.sms.system.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.entity.Role;
import org.springframework.stereotype.Service;
import com.gdut.sms.system.repository.RoleRepository;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 角色管理核心服务
 * @author ckx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Cacheable(value = "role_list", unless = "#result == null || #result.empty")
    public List<?> list() {
        class RoleDTO {
            final String roleCode;
            final String roleName;

            RoleDTO(Role role) {
                roleCode = role.getRoleCode();
                roleName = role.getRoleName();
            }
        }

        return roleRepository.findAll().stream()
                .map(RoleDTO::new)
                .toList();
    }

}
