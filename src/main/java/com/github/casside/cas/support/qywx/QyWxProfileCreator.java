package com.github.casside.cas.support.qywx;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

/**
 *
 */
public class QyWxProfileCreator extends OAuth20ProfileCreator<OAuth20Profile> {

    public QyWxProfileCreator(OAuth20Configuration configuration, IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected void signRequest(final OAuth20Service service, OAuth2AccessToken accessToken,
                               final OAuthRequest request) {
        if (accessToken == null) {
            // pac4j oauth 不允许 accessToken 为 null，所以这里设置为空字符串
            accessToken = new OAuth2AccessToken("");
            throw new RuntimeException();
        }
        service.signRequest(accessToken, request);
        if (this.configuration.isTokenAsHeader()) {
            request.addHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX + accessToken.getAccessToken());
        }
        if (Verb.POST.equals(request.getVerb())) {
            request.addParameter(OAuthConfiguration.OAUTH_TOKEN, accessToken.getAccessToken());
        }

        // release code
        request.addParameter("code", QyWxCodeOnceHolder.release());
    }

}
