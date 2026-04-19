package com.gdut.sms.system.controller;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.system.service.RoleService;
import org.springframework.http.ResponseEntity;
import com.gdut.sms.common.annotation.OperationLogging;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理微服务controller
 * @author ckx
 */
@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/")
    @OperationLogging(module = "用户管理", type = "查询", desc = "获取角色列表")
    public ResponseEntity<?> getRoles() {
        try {
            return ResponseEntity.ok(roleService.list());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
