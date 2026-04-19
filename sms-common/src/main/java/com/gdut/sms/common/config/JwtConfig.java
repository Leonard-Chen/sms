package com.gdut.sms.common.config;

import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * JWT token自定义配置类，将权限塞进token里
 * @author ckx
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    /**
     * 权限解析器
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object o = jwt.getClaims().get("authorities");
            if (o instanceof Collection<?>) {
                return ((Collection<?>) o).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
        return converter;
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                Authentication auth = context.getPrincipal();

                if (auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
                    context.getClaims()
                            .claim("authorities", auth.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .toList());
                }

                context.getClaims().claim("username", auth.getName());

                if (auth.getPrincipal() instanceof User user) {
                    if (user.getDept() != null) {
                        context.getClaims().claim("deptNo", user.getDept().getDeptNo());
                    }
                }
            }
        };
    }

}
