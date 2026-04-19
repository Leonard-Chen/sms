package com.gdut.sms.order.controller;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.dto.OrderDTO;
import com.gdut.sms.common.dto.AssignRequest;
import com.gdut.sms.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import com.gdut.sms.common.annotation.OperationLogging;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDateTime;

/**
 * 订单管理微服务controller
 * @author ckx
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/")
    @OperationLogging(module = "订单管理", type = "查询", desc = "获取订单列表")
    public ResponseEntity<?> list(@AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(orderService.list(
                            jwt.getSubject(),
                            jwt.getClaim("deptNo"),
                            jwt.getClaimAsStringList("authorities")
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{no}")
    @OperationLogging(module = "订单管理", type = "查询", desc = "查看指定订单")
    public ResponseEntity<?> get(@PathVariable String no) {
        try {
            return ResponseEntity.ok(orderService.get(no));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{deptNo}")
    @OperationLogging(module = "订单管理", type = "查询", desc = "查看某个部门的所有订单")
    public ResponseEntity<?> getByDeptNo(@PathVariable String deptNo) {
        try {
            return ResponseEntity.ok(orderService.getByDept(deptNo));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/")
    @OperationLogging(module = "订单管理", type = "创建", desc = "新增订单")
    public ResponseEntity<?> create(@RequestBody OrderDTO order,
                                    @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(orderService.create(order, jwt.getSubject()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/")
    @OperationLogging(module = "订单管理", type = "更新", desc = "更新订单")
    public ResponseEntity<?> update(@RequestBody OrderDTO order) {
        try {
            return ResponseEntity.ok(orderService.update(order));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/audit/{no}")
    @OperationLogging(module = "订单管理", type = "审核", desc = "审核某个订单")
    public ResponseEntity<?> audit(@PathVariable String no,
                                   @RequestParam Integer status,
                                   @RequestParam String remarks,
                                   @AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(orderService.audit(no, status, remarks, jwt.getSubject()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{no}")
    @OperationLogging(module = "订单管理", type = "删除", desc = "删除某个订单")
    public ResponseEntity<?> delete(@PathVariable String no) {
        try {
            orderService.delete(no);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/schedule/recommend/{orderNo}")
    @OperationLogging(module = "调度单管理", type = "查询", desc = "获取推荐员工列表")
    public ResponseEntity<?> recommend(@PathVariable String orderNo) {
        try {
            return ResponseEntity.ok(orderService.recommendStaff(orderNo));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/schedule/assign")
    @OperationLogging(module = "调度单管理", type = "创建", desc = "分派订单，新增调度")
    public ResponseEntity<?> assign(@RequestBody AssignRequest req) {
        try {
            return ResponseEntity.ok(orderService.assign(req));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/schedule/accept/{scheduleNo}")
    @OperationLogging(module = "调度单管理", type = "更新", desc = "接受调度")
    public ResponseEntity<?> accept(@PathVariable String scheduleNo) {
        try {
            return ResponseEntity.ok(orderService.accept(scheduleNo));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/schedule/{orderNo}")
    @OperationLogging(module = "调度单管理", type = "查询", desc = "获取某一订单的调度信息")
    public ResponseEntity<?> scheduleByOrder(@PathVariable String orderNo) {
        try {
            return ResponseEntity.ok(orderService.getScheduleByOrderNo(orderNo));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/schedule/complete/{scheduleNo}")
    @OperationLogging(module = "调度单管理", type = "更新", desc = "完成订单调度")
    public ResponseEntity<?> complete(@PathVariable String scheduleNo) {
        try {
            return ResponseEntity.ok(orderService.complete(scheduleNo));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    @OperationLogging(module = "订单管理", type = "查询", desc = "统计订单数量")
    public ResponseEntity<?> count() {
        try {
            return ResponseEntity.ok(orderService.count());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/count/finished")
    @OperationLogging(module = "订单管理", type = "查询", desc = "统计已完成订单数量")
    public ResponseEntity<?> countFinished() {
        try {
            return ResponseEntity.ok(orderService.countFinishedOrders());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/count/stats")
    @OperationLogging(module = "订单管理", type = "查询", desc = "分别统计已完成、服务中、待审核、已取消的订单数量")
    public ResponseEntity<?> countForStatistics() {
        try {
            return ResponseEntity.ok(new Long[] {
                    orderService.countFinishedOrders(),
                    orderService.countPendingOrders(),
                    orderService.countOrdersToBeAudited(),
                    orderService.countCanceledOrders()
            });
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/sum")
    @OperationLogging(module = "订单管理", type = "查询", desc = "统计某一时间段内的订单总额")
    public ResponseEntity<?> sumAmountBetween(@RequestParam LocalDateTime start,
                                              @RequestParam LocalDateTime end) {
        try {
            return ResponseEntity.ok(orderService.sumAmountBetween(start, end));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/monthly")
    @OperationLogging(module = "订单管理", type = "查询", desc = "获取某一年的月度数据")
    public ResponseEntity<?> monthly(@RequestParam Integer year) {
        try {
            return ResponseEntity.ok(orderService.getMonthlyStatistics(year));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
