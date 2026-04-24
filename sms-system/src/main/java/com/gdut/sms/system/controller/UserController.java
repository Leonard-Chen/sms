package com.gdut.sms.system.controller;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.dto.UserDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.gdut.sms.system.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gdut.sms.common.annotation.OperationLogging;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 用户管理微服务controller
 * @author ckx
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "获取用户列表", description = "查询所有用户信息")
    @GetMapping("/")
    @OperationLogging(module = "用户管理", type = "查看", desc = "获取用户列表")
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(userService.list());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "获取某个用户", description = "查询某个用户信息")
    @GetMapping("/{username}")
    @OperationLogging(module = "用户管理", type = "查看", desc = "查看某个用户")
    public ResponseEntity<?> get(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.get(username));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "删除用户", description = "删除某个用户")
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

    @Operation(summary = "新增用户", description = "新增一个用户")
    @PostMapping("/")
    @OperationLogging(module = "用户管理", type = "新增", desc = "新增一个用户")
    public ResponseEntity<?> register(@RequestBody UserDTO dto) {
        try {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
            return ResponseEntity.ok(userService.create(dto));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "更新用户", description = "更新某个用户的信息")
    @PutMapping("/")
    @OperationLogging(module = "用户管理", type = "更新", desc = "更新某个用户")
    public ResponseEntity<?> update(@RequestBody UserDTO dto) {
        try {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
            return ResponseEntity.ok(userService.update(dto));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
