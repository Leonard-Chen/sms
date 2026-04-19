package com.gdut.sms.ai.config;

import org.springframework.web.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * AI HTTP 客户端配置
 * @author ly
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class HttpClientConfig {

    @Bean
    public RestClient restClient(AiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
