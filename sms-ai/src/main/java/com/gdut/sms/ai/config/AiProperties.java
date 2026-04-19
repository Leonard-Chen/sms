package com.gdut.sms.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 配置属性
 * @author ly
 */
@Data
@ConfigurationProperties(prefix = "ai")
public class AiProperties {
    private String provider = "openai-compatible";
    private String baseUrl = "https://api.openai.com/v1";
    private String apiKey = "please_replace_me";
    private String model = "gpt-4o-mini";
    private int timeoutMs = 30000;
}
