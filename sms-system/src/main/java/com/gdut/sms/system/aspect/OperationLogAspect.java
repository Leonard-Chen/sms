package com.gdut.sms.system.aspect;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.entity.User;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import com.gdut.sms.common.entity.OperationLog;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import com.gdut.sms.system.repository.UserRepository;
import com.gdut.sms.common.annotation.OperationLogging;
import org.springframework.security.core.Authentication;
import com.gdut.sms.system.repository.OperationLogRepository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 操作日志记录切面
 *
 * @author ckx
 */
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final UserRepository userRepository;

    private final OperationLogRepository operationLogRepository;

    @Around("@annotation(logging)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLogging logging) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            saveLog(joinPoint, logging, start);
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, OperationLogging logging, long start) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("未知错误");
        }
        HttpServletRequest request = attributes.getRequest();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = auth != null && auth.isAuthenticated()
                ? userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("错误的用户"))
                : null;

        OperationLog log = new OperationLog();

        log.setUser(user);
        log.setModule(logging.module());
        log.setOperationType(logging.type());
        log.setOperationDesc(logging.desc());

        log.setRequestMethod(request.getMethod());
        log.setRequestUrl(request.getRequestURI());
        log.setRequestParams(JSON.toJSONString(joinPoint.getArgs()));
        log.setIpAddress(getClientIP(request));
        log.setTime(System.currentTimeMillis() - start);
        log.setLogTime(LocalDateTime.now());

        new Thread(() -> operationLogRepository.save(log)).start();
    }

    /**
     * 从客户端请求中获取真实ip地址，而非直接通过getRemoteAddr()获取（因为可能是代理服务器的地址）
     *
     * @param request HttpServlet请求
     * @return 客户端ip地址
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            for (String s : ip.split(",")) {
                if (!"unknown".equalsIgnoreCase(s)) {
                    ip = s.trim();
                    break;
                }
            }
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
