package com.gdut.sms.employee.controller;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.dto.EmployeeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import com.gdut.sms.employee.service.EmployeeService;
import com.gdut.sms.common.annotation.OperationLogging;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 员工管理微服务controller
 * @author ckx
 */
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/")
    @OperationLogging(module = "员工管理", type = "查询", desc = "获取员工列表")
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(employeeService.list());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{no}")
    @OperationLogging(module = "员工管理", type = "查询", desc = "查看某个员工")
    public ResponseEntity<?> get(@PathVariable String no) {
        try {
            return ResponseEntity.ok(employeeService.get(no));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/")
    @OperationLogging(module = "员工管理", type = "创建", desc = "新增员工信息")
    public ResponseEntity<?> create(@RequestBody EmployeeDTO employee,
                                   @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(employeeService.create(employee, jwt.getSubject()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/")
    @OperationLogging(module = "员工管理", type = "更新", desc = "更新员工信息")
    public ResponseEntity<?> update(@RequestBody EmployeeDTO employee) {
        try {
            return ResponseEntity.ok(employeeService.update(employee));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{no}")
    @OperationLogging(module = "员工管理", type = "更新", desc = "更新员工部分信息：状态")
    public ResponseEntity<?> updateWorkStatus(@PathVariable String no,
                                              @RequestParam Integer status) {
        try {
            return ResponseEntity.ok(employeeService.updateWorkStatus(no, status));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{no}")
    @OperationLogging(module = "员工管理", type = "删除", desc = "删除员工信息")
    public ResponseEntity<?> delete(@PathVariable String no) {
        try {
            employeeService.delete(no);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
