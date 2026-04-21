package com.gdut.sms.customer.controller;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.dto.CustomerDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "客户管理", description = "客户管理相关接口")
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "获取客户列表", description = "查询所有客户信息")
    @GetMapping("/")
    @OperationLogging(module = "客户管理", type = "查看所有", desc = "获取客户列表")
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(customerService.list());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "获取某个客户", description = "查询某个客户的信息")
    @GetMapping("/{no}")
    @OperationLogging(module = "客户管理", type = "查看某个", desc = "查看指定用户")
    public ResponseEntity<?> get(@PathVariable String no) {
        try {
            return ResponseEntity.ok(customerService.get(no));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "新增客户", description = "新增一个客户")
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

    @Operation(summary = "删除客户", description = "删除某个客户")
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

    @Operation(summary = "统计客户", description = "统计客户总数")
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
