package com.github.casside.cas.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.model.support.pac4j.Pac4jDelegatedAuthenticationProperties;
import org.pac4j.core.client.BaseClient;
import org.springframework.util.CollectionUtils;

/**
 * 重写 代理client工厂，添加企业微信Client
 */
@Slf4j
public class DelegatedClientFactory extends org.apereo.cas.support.pac4j.authentication.DelegatedClientFactory {

    /**
     * 注入自定义的 client
     */
    private List<BaseClient> customClients = new ArrayList<>();

    public DelegatedClientFactory(Pac4jDelegatedAuthenticationProperties pac4jProperties, List<BaseClient> customClients) {
        super(pac4jProperties);
        if (!CollectionUtils.isEmpty(customClients)) {
            this.customClients.addAll(customClients);
        }
    }

    @Override
    public Set<BaseClient> build() {
        Set<BaseClient> clients = super.build();
        clients.addAll(this.customClients);
        return clients;
    }
}
