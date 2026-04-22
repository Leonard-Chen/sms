package com.gdut.sms.statistics.controller;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import com.gdut.sms.statistics.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 营业额统计微服务controller
 * @author ckx
 */
@Tag(name = "数据统计", description = "数据统计相关接口")
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "核心指标", description = "获取当年或某一年的核心业务数据，如订单数量、营业额等")
    @GetMapping("/core")
    public ResponseEntity<?> getData(@RequestParam(required = false) Integer year) {
        try {
            return ResponseEntity.ok(statisticsService.getStatistics(year));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "订单数据", description = "获取各类订单的数据，用于计算不同类型订单的比例")
    @GetMapping("/order")
    public ResponseEntity<?> getOrderData(@RequestParam(required = false) Integer year) {
        try {
            return ResponseEntity.ok(statisticsService.getOrderStatistics(year));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "月度数据", description = "获取某一年的月度数据，包括各月的订单数和总营业额")
    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyData(@RequestParam Integer year) {
        try {
            return ResponseEntity.ok(statisticsService.getMonthlyStatistics(year));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "年度数据", description = "获取企业往年的数据，包括每年的累计订单数和营业额")
    @GetMapping("/annually")
    public ResponseEntity<?> getAnnuallyData(@RequestParam Integer start,
                                             @RequestParam Integer end) {
        try {
            return ResponseEntity.ok(statisticsService.getAnnuallyStatistics(start, end));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
