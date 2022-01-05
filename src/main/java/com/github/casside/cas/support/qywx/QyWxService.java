package com.github.casside.cas.support.qywx;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;
import java.io.OutputStream;

/**
 * 作用：<br>
 *
 * - 构建 access_token 请求 <br>
 *
 * - 维护 access_token <br>
 *
 * - 构建 refresh_token 请求 <br>
 *
 * - 请求签名 <br>
 */
public class QyWxService extends OAuth20Service {

    public QyWxService(DefaultApi20 api, String apiKey, String apiSecret, String callback, String defaultScope,
                       String responseType, OutputStream debugStream, String userAgent, HttpClientConfig httpClientConfig,
                       HttpClient httpClient) {
        super(api, apiKey, apiSecret, callback, defaultScope, responseType, debugStream, userAgent, httpClientConfig, httpClient);
    }

    @Override
    public String getVersion() {
        return "2.0";
    }

    @Override
    protected OAuthRequest createAccessTokenRequest(AccessTokenRequestParams params) {
        String       code  = params.getCode();
        String       scope = params.getScope();
        DefaultApi20 api   = getApi();

        final OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());

        api.getClientAuthentication().addClientAuthentication(request, getApiKey(), getApiSecret());

        request.addParameter(OAuthConstants.REDIRECT_URI, getCallback());
        request.addParameter("corpid", getApiKey());
        request.addParameter("corpsecret", getApiSecret());
        if (scope != null) {
            request.addParameter(OAuthConstants.SCOPE, scope);
        }
        request.addParameter(OAuthConstants.GRANT_TYPE, OAuthConstants.AUTHORIZATION_CODE);
        return request;
    }
}
