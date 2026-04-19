package com.gdut.sms.customer.controller;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.dto.CustomerDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import com.gdut.sms.customer.service.CustomerService;
import com.gdut.sms.common.annotation.OperationLogging;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 客户管理微服务controller
 * @author ckx
 */
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/")
    @OperationLogging(module = "客户管理", type = "查看所有", desc = "获取客户列表")
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(customerService.list());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{no}")
    @OperationLogging(module = "客户管理", type = "查看某个", desc = "查看指定用户")
    public ResponseEntity<?> get(@PathVariable String no) {
        try {
            return ResponseEntity.ok(customerService.get(no));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/")
    @OperationLogging(module = "客户管理", type = "创建", desc = "新增客户信息")
    public ResponseEntity<?> create(@RequestBody CustomerDTO customer,
                            @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(customerService.create(customer, jwt.getSubject()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{no}")
    @OperationLogging(module = "客户管理", type = "删除", desc = "删除客户信息")
    public ResponseEntity<?> delete(@PathVariable String no) {
        try {
            customerService.delete(no);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    @OperationLogging(module = "客户管理", type = "查询", desc = "统计客户数量")
    public ResponseEntity<?> count() {
        try {
            return ResponseEntity.ok(customerService.count());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
