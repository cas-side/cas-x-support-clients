package com.github.casside.cas.support;

import com.github.casside.cas.support.qywx.QyWxClient;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.pac4j.core.client.IndirectClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 构建 QyWxClient
 *
 * @see QyWxClient
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(DelagatedClientProperties.class)
@ConditionalOnProperty(value = "cas-x.authn.enabled", havingValue = "true", matchIfMissing = true)
public class ClientsConfiguration {

    private final CasConfigurationProperties casProperties;

    /**
     * 必须重写这个bean，因为需要添加自定义client
     *
     * @see org.apereo.cas.support.pac4j.config.support.authentication.Pac4jAuthenticationEventExecutionPlanConfiguration#pac4jDelegatedClientFactory
     */
    @Bean(name = "pac4jDelegatedClientFactory")
    public DelegatedClientFactory pac4jDelegatedClientFactory(@Autowired(required = false) List<IndirectClient> customClients) {
        return new DelegatedClientFactory(casProperties, customClients);
    }


}
