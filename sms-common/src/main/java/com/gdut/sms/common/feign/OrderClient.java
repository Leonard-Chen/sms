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
    ResponseEntity<Long> count();

    @GetMapping("/count/finished")
    ResponseEntity<Long> countFinished();

    @GetMapping("/count/stats")
    ResponseEntity<Long[]> countForStatistics();

    @GetMapping("/sum")
    ResponseEntity<BigDecimal> sumAmountBetween(@RequestParam LocalDateTime start,
                                                @RequestParam LocalDateTime end);

    @GetMapping("/monthly")
    ResponseEntity<List<Object[]>> monthly(@RequestParam Integer year);

}
