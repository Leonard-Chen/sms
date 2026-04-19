package com.gdut.sms.ai.service;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.ai.dto.AiChatRequest;
import com.gdut.sms.ai.dto.AiChatResponse;
import org.springframework.http.MediaType;
import com.gdut.sms.ai.config.AiProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * AI 对话业务 service
 * @author ly
 */
@Service
@RequiredArgsConstructor
public class AiService {

    private final RestClient restClient;
    private final AiProperties properties;

    public AiChatResponse chat(AiChatRequest req) {
        String apiKey = normalizeApiKey(properties.getApiKey());
        if (apiKey == null || apiKey.isBlank() || "please_replace_me".equals(apiKey)) {
            throw new RuntimeException("AI API Key 未配置，请设置 AI_API_KEY 或 ai.api-key");
        }

        String sessionId = (req.getSessionId() == null || req.getSessionId().isBlank())
                ? UUID.randomUUID().toString()
                : req.getSessionId();

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(message("system", "你是订单调度助手，擅长订单流转、人员分派、异常工单处理与业务答疑。请使用简洁专业的中文回答，并优先给出可执行建议。"));
        if (req.getHistory() != null) {
            for (AiChatRequest.MessageItem item : req.getHistory()) {
                if (item == null) continue;
                String role = item.getRole() == null ? "user" : item.getRole().trim();
                String content = item.getContent() == null ? "" : item.getContent().trim();
                if (!content.isBlank()) {
                    messages.add(message(role, content));
                }
            }
        }
        messages.add(message("user", req.getMessage().trim()));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", properties.getModel());
        payload.put("messages", messages);
        payload.put("temperature", 0.3);
        payload.put("stream", false);

        Map<String, Object> body;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);
            body = resp;
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RuntimeException("AI 鉴权失败(401)：请检查 AI_API_KEY 是否正确，且不要包含前缀 'Bearer '");
        }

        String reply = extractReply(body);
        return new AiChatResponse(sessionId, reply, properties.getModel());
    }

    private static String normalizeApiKey(String raw) {
        if (raw == null) return null;
        String key = raw.trim();
        if (key.regionMatches(true, 0, "Bearer ", 0, 7)) {
            key = key.substring(7).trim();
        }
        return key;
    }

    private static Map<String, String> message(String role, String content) {
        Map<String, String> msg = new LinkedHashMap<>();
        msg.put("role", role);
        msg.put("content", content);
        return msg;
    }

    @SuppressWarnings("unchecked")
    private static String extractReply(Map<String, Object> body) {
        if (body == null) {
            throw new RuntimeException("AI 响应为空");
        }
        Object choicesObj = body.get("choices");
        if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) {
            throw new RuntimeException("AI 响应格式不正确");
        }
        Object first = choices.get(0);
        if (!(first instanceof Map<?, ?> firstMap)) {
            throw new RuntimeException("AI 响应格式不正确");
        }
        Object messageObj = firstMap.get("message");
        if (!(messageObj instanceof Map<?, ?> msgMap)) {
            throw new RuntimeException("AI 响应格式不正确");
        }
        Object content = msgMap.get("content");
        if (content == null) {
            throw new RuntimeException("AI 未返回内容");
        }
        return String.valueOf(content).trim();
    }
}
