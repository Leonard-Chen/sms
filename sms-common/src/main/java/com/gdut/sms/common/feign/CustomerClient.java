package com.gdut.sms.common.feign;

import com.gdut.sms.common.config.FeignConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "sms-customer", path = "/customer", configuration = FeignConfig.class)
public interface CustomerClient {

    @GetMapping("/count")
    ResponseEntity<Long> count(@RequestParam(required = false) Integer year);

}
