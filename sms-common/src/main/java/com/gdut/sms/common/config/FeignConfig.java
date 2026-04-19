package com.gdut.sms.common.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 跨服务调用配置
 * @author ckx
 */
@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    /**
     * 微服务调用过程中传递token
     */
    @Bean
    public RequestInterceptor requestInterceptor() throws RuntimeException {

        return template -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                throw new RuntimeException("未知错误");
            }

            HttpServletRequest request = attributes.getRequest();

            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                template.header("Authorization", token);
            }
        };
    }

}
