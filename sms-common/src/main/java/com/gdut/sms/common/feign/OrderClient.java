package com.gdut.sms.common.feign;

import com.gdut.sms.common.config.FeignConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@FeignClient(value = "sms-order", path = "/order", configuration = FeignConfig.class)
public interface OrderClient {

    @GetMapping("/count")
    ResponseEntity<Long> count(@RequestParam(required = false) Integer year);

    @GetMapping("/count/finished")
    ResponseEntity<Long> countFinished(@RequestParam(required = false) Integer year);

    @GetMapping("/count/stats")
    ResponseEntity<Long[]> countForStatistics(@RequestParam(required = false) Integer year);

    @GetMapping("/sum")
    ResponseEntity<BigDecimal> sumAmountBetween(@RequestParam(required = false) LocalDateTime start,
                                                @RequestParam(required = false) LocalDateTime end);

    @GetMapping("/monthly")
    ResponseEntity<List<Object[]>> monthly(@RequestParam Integer year);

    @GetMapping("/annually")
    ResponseEntity<List<Object[]>> annually(@RequestParam Integer start,
                                            @RequestParam Integer end);

}
