package com.gdut.sms.system;

import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCaching
@EntityScan(basePackages = {"com.gdut.sms.common.entity"})
public class SmsSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmsSystemApplication.class, args);
    }
}
