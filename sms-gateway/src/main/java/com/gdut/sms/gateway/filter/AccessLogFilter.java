package com.gdut.sms.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

/**
 * 请求拦截，记录相关信息
 * @author ckx
 */
@Component
public class AccessLogFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        String ip = request.getRemoteAddress().getAddress().getHostAddress();
        long start = System.currentTimeMillis();

        log.info("请求开始: {} {}, ip: {}", method, path, ip);

        return chain.filter(exchange).doFinally(signalType -> {
            long time = System.currentTimeMillis() - start;
            HttpStatus status = (HttpStatus) exchange.getResponse().getStatusCode();
            log.info("请求结束: {} {}, ip: {}, 状态码: {}, 耗时: {} ms", method, path, ip, status, time);
        });
    }

    @Override
    public int getOrder() {
        return -99;
    }
}
