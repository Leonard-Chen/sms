package com.gdut.sms.statistics.controller;

import lombok.RequiredArgsConstructor;
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
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/core")
    public ResponseEntity<?> getData() {
        try {
            return ResponseEntity.ok(statisticsService.getStatistics());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/order")
    public ResponseEntity<?> getOrderData() {
        try {
            return ResponseEntity.ok(statisticsService.getOrderStatistics());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyData(@RequestParam Integer year) {
        try {
            return ResponseEntity.ok(statisticsService.getMonthlyStatistics(year));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
