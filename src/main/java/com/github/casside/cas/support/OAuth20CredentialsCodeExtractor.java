package com.github.casside.cas.support;

import com.github.casside.cas.support.qywx.QyWxCodeOnceHolder;
import java.util.Optional;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.credentials.extractor.OAuth20CredentialsExtractor;

/**
 * 提取credentials的同时，提取code并临时保存
 */
public class OAuth20CredentialsCodeExtractor extends OAuth20CredentialsExtractor {

    public OAuth20CredentialsCodeExtractor(OAuth20Configuration configuration, IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected Optional<OAuth20Credentials> getOAuthCredentials(WebContext context) {
        Optional<OAuth20Credentials> opt = super.getOAuthCredentials(context);
        opt.ifPresent(oAuthCredentials -> {
            // hold 住 code
            String code = oAuthCredentials.getCode();
            QyWxCodeOnceHolder.set(code);
        });

        return opt;
    }
}
