package com.gdut.sms.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCaching
@EnableFeignClients(basePackages = {"com.gdut.sms.common.feign"})
public class SmsStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsStatisticsApplication.class, args);
    }

}
