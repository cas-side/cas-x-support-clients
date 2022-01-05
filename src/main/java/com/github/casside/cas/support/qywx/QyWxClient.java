package com.github.casside.cas.support.qywx;

import com.github.casside.cas.support.OAuth20CredentialsCodeExtractor;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.pac4j.oauth.client.OAuth20Client;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.credentials.authenticator.OAuth20Authenticator;
import org.pac4j.oauth.redirect.OAuth20RedirectionActionBuilder;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class QyWxClient extends OAuth20Client {

    /**
     * 相当于主键ID
     */
    private String profileId;
    /**
     * attribute release
     */
    Map<String, String> profileAttrs;
    /**
     * 跳转到授权URL的时候需要加的自定义参数
     */
    Map<String, String> customParams;

    public QyWxClient(final String key, final String secret, final QyWxProfileDefinition qyWxProfileDefinition) {

        this.setConfiguration(new OAuth20Configuration());
        this.setKey(key);
        this.setSecret(secret);
        this.configuration.setProfileDefinition(qyWxProfileDefinition);
    }

    /**
     * 有点懒加载的意思，可以放到构造函数
     */
    @Override
    protected void clientInit() {
        configuration.setApi(QyWxApi.instance());
        configuration.setCustomParams(this.getCustomParams());

        QyWxProfileDefinition profileDefinition = (QyWxProfileDefinition) this.configuration.getProfileDefinition();
        profileDefinition.setProfileId(profileId);
        this.profileAttrs.forEach(profileDefinition::addProfileAttribute);

        defaultRedirectionActionBuilder(new OAuth20RedirectionActionBuilder(configuration, this));
        defaultCredentialsExtractor(new OAuth20CredentialsCodeExtractor(configuration, this));
        defaultAuthenticator(new OAuth20Authenticator(configuration, this));
        defaultProfileCreator(new QyWxProfileCreator(configuration, this));

        super.clientInit();
    }

}
