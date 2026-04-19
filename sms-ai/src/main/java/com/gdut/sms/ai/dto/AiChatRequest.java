package com.gdut.sms.ai.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.ArrayList;

/**
 * AI 对话请求 DTO
 * @author ly
 */
@Data
public class AiChatRequest {
    private String sessionId;

    @NotBlank(message = "message 不能为空")
    private String message;

    private List<MessageItem> history = new ArrayList<>();

    @Data
    public static class MessageItem {
        private String role;
        private String content;
    }
}
