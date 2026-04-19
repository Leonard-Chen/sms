package com.gdut.sms.ai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * AI 对话响应 DTO
 * @author ly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponse {
    private String sessionId;
    private String reply;
    private String model;
}
