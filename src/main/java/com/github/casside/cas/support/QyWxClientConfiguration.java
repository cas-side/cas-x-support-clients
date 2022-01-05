package com.github.casside.cas.support;

import com.github.casside.cas.support.qywx.QyWxAuthenticationAction;
import com.github.casside.cas.support.qywx.QyWxClient;
import com.github.casside.cas.support.qywx.QyWxProfileDefinition;
import com.github.casside.cas.support.qywx.QyWxProperties;
import com.github.casside.cas.support.qywx.UserProfileService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.principal.ClientCustomPropertyConstants;
import org.apereo.cas.configuration.model.support.pac4j.Pac4jBaseClientProperties;
import org.apereo.cas.web.DelegatedClientWebflowManager;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Clients;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置企业微信Client
 */
@Configuration
@EnableConfigurationProperties(QyWxProperties.class)
@ConditionalOnProperty(value = "cas-x.authn.qy_wx.enabled", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class QyWxClientConfiguration {

    private final DelagatedClientProperties delagatedClientProperties;

    private final UserProfileService userProfileService;

    @Bean
    public QyWxProfileDefinition qyWxProfileDefinition() {
        return new QyWxProfileDefinition(userProfileService);
    }

    @Bean
    public QyWxClient qyWxClient(QyWxProfileDefinition qyWxProfileDefinition) {
        QyWxProperties   props  = delagatedClientProperties.getQyWx();
        final QyWxClient client = new QyWxClient(props.getId(), props.getSecret(), qyWxProfileDefinition);
        configureClient(client, props);
        client.setCustomParams(props.getCustomParams());
        // profile id field name
        client.setProfileId(props.getPrincipalAttributeId());
        // profile attributes to release
        client.setProfileAttrs(props.getProfileAttrs());
        log.debug("Created client [{}] with identifier [{}]", client.getName(), client.getKey());

        return client;
    }

    @Bean
    public QyWxAuthenticationAction qyWxAuthenticationAction(Clients clients,
                                                             DelegatedClientWebflowManager delegatedClientWebflowManager,
                                                             BeanFactory beanFactory) {
        return new QyWxAuthenticationAction(delagatedClientProperties, clients, delegatedClientWebflowManager, beanFactory);
    }


    /**
     * @see org.apereo.cas.support.pac4j.authentication.DelegatedClientFactory#configureClient(BaseClient, Pac4jBaseClientProperties)
     */
    private void configureClient(final BaseClient client, final Pac4jBaseClientProperties props) {
        if (StringUtils.isNotBlank(props.getClientName())) {
            client.setName(props.getClientName());
        }
        final Map customProperties = client.getCustomProperties();
        customProperties.put(ClientCustomPropertyConstants.CLIENT_CUSTOM_PROPERTY_AUTO_REDIRECT, props.isAutoRedirect());
        if (StringUtils.isNotBlank(props.getPrincipalAttributeId())) {
            customProperties.put(ClientCustomPropertyConstants.CLIENT_CUSTOM_PROPERTY_PRINCIPAL_ATTRIBUTE_ID, props.getPrincipalAttributeId());
        }
    }

}
