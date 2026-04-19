package com.gdut.sms.system.controller;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.system.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gdut.sms.common.annotation.OperationLogging;

/**
 * 用户管理微服务controller
 * @author ckx
 */
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    @OperationLogging(module = "用户管理", type = "查看", desc = "获取用户列表")
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(userService.list());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{username}")
    @OperationLogging(module = "用户管理", type = "查看", desc = "查看某个用户")
    public ResponseEntity<?> get(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.get(username));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{username}")
    @OperationLogging(module = "用户管理", type = "删除", desc = "删除某个用户")
    public ResponseEntity<?> delete(@PathVariable String username) {
        try {
            userService.delete(username);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
