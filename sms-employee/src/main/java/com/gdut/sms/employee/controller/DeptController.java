package com.gdut.sms.employee.controller;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.dto.DeptDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gdut.sms.employee.service.DeptService;
import org.springframework.security.oauth2.jwt.Jwt;
import com.gdut.sms.common.annotation.OperationLogging;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 员工管理微服务controller
 * @author ckx
 */
@RestController
@RequestMapping("/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @GetMapping("/")
    @OperationLogging(module = "部门管理", type = "查询", desc = "获取部门列表")
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(deptService.list());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{no}")
    @OperationLogging(module = "部门管理", type = "查询", desc = "查询部门信息")
    public ResponseEntity<?> get(@PathVariable String no) {
        try {
            return ResponseEntity.ok(deptService.get(no));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/")
    @OperationLogging(module = "部门管理", type = "创建", desc = "新增部门")
    public ResponseEntity<?> create(@RequestBody DeptDTO dept,
                                    @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(deptService.create(dept, jwt.getSubject()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/")
    @OperationLogging(module = "员工管理", type = "更新", desc = "更新部门信息")
    public ResponseEntity<?> update(@RequestBody DeptDTO dept) {
        try {
            return ResponseEntity.ok(deptService.update(dept));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{no}")
    @OperationLogging(module = "部门管理", type = "删除", desc = "删除部门")
    public ResponseEntity<?> delete(@PathVariable String no) {
        try {
            deptService.delete(no);
            return ResponseEntity.ok("删除成功");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
