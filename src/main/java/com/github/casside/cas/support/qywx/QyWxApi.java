package com.github.casside.cas.support.qywx;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.ParameterList;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignature;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignatureURIQueryParameter;
import java.io.OutputStream;
import java.util.Map;

public class QyWxApi extends DefaultApi20 {

    protected QyWxApi() {
    }

    public static QyWxApi instance() {
        return QyWxApi.InstanceHolder.INSTANCE;
    }

    public Verb getAccessTokenVerb() {
        return Verb.GET;
    }

    public String getProfileUrl() {
        return "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";
    }

    public String getAccessTokenEndpoint() {
        return "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
    }

    protected String getAuthorizationBaseUrl() {
        return "https://open.work.weixin.qq.com/wwopen/sso/qrConnect";
    }

    /**
     * 签名类型，一种是在header头加AUTHORIZATION，一种是在请求参数中
     */
    @Override
    public BearerSignature getBearerSignature() {
        return BearerSignatureURIQueryParameter.instance();
    }

    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return QyWxTokenExtractor.INSTANCE;
    }

    @Override
    public String getAuthorizationUrl(String responseType, String apiKey, String callback, String scope, String state,
                                      Map<String, String> additionalParams) {

        final ParameterList parameters = new ParameterList(additionalParams);
        parameters.add(OAuthConstants.RESPONSE_TYPE, responseType);
        parameters.add("appid", apiKey);

        if (callback != null) {
            parameters.add(OAuthConstants.REDIRECT_URI, callback);
        }

        if (scope != null) {
            parameters.add(OAuthConstants.SCOPE, scope);
        }

        if (state != null) {
            parameters.add(OAuthConstants.STATE, state);
        }

        return parameters.appendTo(getAuthorizationBaseUrl());
    }

    @Override
    public OAuth20Service createService(String apiKey, String apiSecret, String callback, String defaultScope, String responseType,
                                        OutputStream debugStream, String userAgent, HttpClientConfig httpClientConfig, HttpClient httpClient) {
        responseType = "token";
        return new QyWxService(this, apiKey, apiSecret, callback, defaultScope, responseType, debugStream, userAgent, httpClientConfig, httpClient);
    }

    private static class InstanceHolder {

        private static final QyWxApi INSTANCE = new QyWxApi();

        private InstanceHolder() {
        }
    }
}
