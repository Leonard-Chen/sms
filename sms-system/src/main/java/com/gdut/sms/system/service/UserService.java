package com.gdut.sms.system.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Caching;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByUsernameWithDeptAndRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
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
            evict = {
                    @CacheEvict(value = "user", key = "#username"),
                    @CacheEvict(value = "user_list", allEntries = true)
            }
    )
    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }
}
