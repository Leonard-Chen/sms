package com.gdut.sms.order;

import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableCaching
@EntityScan(basePackages = {"com.gdut.sms.common.entity"})
@EnableFeignClients(basePackages = {"com.gdut.sms.common.feign"})
public class SmsOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsOrderApplication.class, args);
    }

}
