package com.gdut.sms.system;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.UUID;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    private final RegisteredClientRepository clientRepository;

    @Override
    public void run(String... args) {
        //用户登录及身份验证专用的client
        String clientId = "sms-client";

        if (clientRepository.findByClientId(clientId) == null) {
            RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(clientId)
                    //配置为无密钥认证
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    //仅支持授权码+PKCE
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    //授权回调地址
                    .redirectUri("http://localhost/callback")
                    //登出回调地址
                    .postLogoutRedirectUri("http://localhost/logout")
                    //授权范围
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    //客户端和令牌配置
                    .clientSettings(ClientSettings.builder()
                            .requireAuthorizationConsent(false)//关闭授权确认页面，不要让用户自己手动点一下“同意”再登录
                            .requireProofKey(true)//强制要求PKCE
                            .build()
                    )
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                            .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
                            .accessTokenTimeToLive(Duration.ofMinutes(10))
                            .refreshTokenTimeToLive(Duration.ofDays(7))
                            .reuseRefreshTokens(false)//强制令牌一次性使用，用完即失效
                            .build()
                    )
                    .build();

            clientRepository.save(client);
        }

        //微服务间调用专用的client
        String clientId2 = "sms-service";

        if (clientRepository.findByClientId(clientId2) == null) {
            RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(clientId2)
                    .clientSecret(passwordEncoder.encode(clientId2))
                    //后台服务间调用，使用basic auth或post方式传递密钥
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    //暂不定义授权范围
                    //客户端和令牌配置
                    .clientSettings(ClientSettings.builder()
                            .requireAuthorizationConsent(false)//通常不需要同意，因为是内部服务
                            .build()
                    )
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)//JWT格式
                            .accessTokenTimeToLive(Duration.ofMinutes(30))
                            //client_credentials通常没有refresh_token，因为太繁琐
                            .build()
                    )
                    .build();

            clientRepository.save(client);
        }
    }
}
