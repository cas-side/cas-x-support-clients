package com.github.casside.cas.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.support.pac4j.authentication.DefaultDelegatedClientFactory;
import org.pac4j.core.client.IndirectClient;
import org.springframework.util.CollectionUtils;

/**
 * 重写 代理client工厂，添加企业微信Client
 */
@Slf4j
public class DelegatedClientFactory extends DefaultDelegatedClientFactory {

    /**
     * 注入自定义的 client
     */
    private List<IndirectClient> customClients = new ArrayList<>();

    public DelegatedClientFactory(CasConfigurationProperties casConfigurationProperties, List<IndirectClient> customClients) {
        super(casConfigurationProperties, Collections.emptyList());
        if (!CollectionUtils.isEmpty(customClients)) {
            this.customClients.addAll(customClients);
        }
    }

    @Override
    public Collection<IndirectClient> build() {
        Collection<IndirectClient> clients = super.build();
        clients.addAll(this.customClients);
        return clients;
    }
}
