package com.gdut.sms.gateway.config;

import reactor.core.publisher.Flux;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;

import java.util.Collection;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class GatewayConfig {

    @Bean
    public SecurityWebFilterChain gatewaySecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/oauth2/**", "/.well-known/**", "/auth/**").permitAll()
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers("/sys/**").hasAuthority("ADMIN")
                        //业务接口权限校验
                        .pathMatchers("/order/**").hasAnyAuthority("ADMIN", "USER", "MANAGER", "OWNER")
                        .pathMatchers("/customer/**").hasAnyAuthority("ADMIN", "USER", "MANAGER", "OWNER")
                        .pathMatchers("/employee/**").hasAnyAuthority("ADMIN", "MANAGER", "OWNER")
                        .pathMatchers("/dept/**").hasAnyAuthority("ADMIN", "OWNER")
                        .pathMatchers("/stats/**").hasAnyAuthority("ADMIN", "MANAGER", "OWNER")
                        .pathMatchers("/ai/**").hasAnyAuthority("ADMIN", "USER", "MANAGER", "OWNER")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(gatewayJwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter gatewayJwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object o = jwt.getClaims().get("authorities");

            if (o instanceof String s && !s.isBlank()) {
                return Flux.just(new SimpleGrantedAuthority(s.trim()));
            }

            if (o instanceof Collection<?> authorities) {
                return Flux.fromIterable(authorities)
                        .mapNotNull(a -> {
                            if (a instanceof String str) return str;
                            if (a instanceof Map<?, ?> m) {
                                Object v = m.get("authority");
                                return v == null ? null : String.valueOf(v);
                            }
                            return null;
                        })
                        .filter(str -> str != null && !str.isBlank())
                        .map(SimpleGrantedAuthority::new);
            }

            return Flux.empty();
        });
        return converter;
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("sms-system", r -> r.path("/sys/**", "/auth/**")
                        .uri("http://localhost:9100"))
                .route("sms-customer", r -> r.path("/customer/**")
                        .uri("http://localhost:9200"))
                .route("sms-order", r -> r.path("/order/**")
                        .uri("http://localhost:9300"))
                .route("sms-employee", r -> r.path("/employee/**", "/dept/**")
                        .uri("http://localhost:9400"))
                .route("sms-statistics", r -> r.path("/stats/**")
                        .uri("http://localhost:9500"))
                .route("sms-ai", r -> r.path("/ai/**")
                        .uri("http://localhost:9600"))
                .build();
    }

}
