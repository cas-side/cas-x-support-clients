package com.github.casside.cas.support;

import com.github.casside.cas.support.qywx.QyWxAuthenticationAction;
import com.github.casside.cas.support.qywx.QyWxClient;
import com.github.casside.cas.support.qywx.QyWxProfileDefinition;
import com.github.casside.cas.support.qywx.QyWxProperties;
import com.github.casside.cas.support.qywx.UserProfileService;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.principal.ClientCustomPropertyConstants;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.pac4j.Pac4jBaseClientProperties;
import org.apereo.cas.web.DelegatedClientWebflowManager;
import org.apereo.cas.web.pac4j.DelegatedSessionCookieManager;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Clients;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 构建 QyWxClient
 *
 * @see QyWxClient
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties(DelagatedClientProperties.class)
@ConditionalOnProperty(value = "cas-x.authn.enabled", havingValue = "true", matchIfMissing = true)
public class ClientsConfiguration {

    private DelagatedClientProperties delagatedClientProperties;

    private CasConfigurationProperties casProperties;

    /**
     * 必须重写这个bean，因为需要添加自定义client
     *
     * @see org.apereo.cas.support.pac4j.config.support.authentication.Pac4jAuthenticationEventExecutionPlanConfiguration#pac4jDelegatedClientFactory
     */
    @Bean(name = "pac4jDelegatedClientFactory")
    public DelegatedClientFactory pac4jDelegatedClientFactory(@Autowired(required = false) List<BaseClient> customClients) {
        return new DelegatedClientFactory(casProperties.getAuthn().getPac4j(), customClients);
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

    /**
     * 配置企业微信Client
     */
    @Configuration
    @ConditionalOnProperty(value = "cas-x.authn.qy_wx.enabled", havingValue = "true")
    public class QyWxClientConfiguration {

        /**
         * 企业微信profile service
         */
        @ConditionalOnMissingBean
        @Bean
        public UserProfileService userProfileService() {
            return new UserProfileService();
        }

        @Bean
        public QyWxProfileDefinition qyWxProfileDefinition(UserProfileService userProfileService) {
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
                                                                 DelegatedSessionCookieManager delegatedSessionCookieManager,
                                                                 BeanFactory beanFactory) {
            return new QyWxAuthenticationAction(delagatedClientProperties, clients, delegatedClientWebflowManager, delegatedSessionCookieManager,
                                                beanFactory);
        }

    }

}
