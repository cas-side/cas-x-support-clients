package com.github.casside.cas.support.qywx;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Response;
import java.io.IOException;
import org.json.JSONObject;

/**
 * 企业微信accessToken 解析器
 */
public class QyWxTokenExtractor implements TokenExtractor<OAuth2AccessToken> {

    private QyWxTokenExtractor() {
    }

    final static QyWxTokenExtractor INSTANCE = new QyWxTokenExtractor();

    @Override
    public OAuth2AccessToken extract(Response response) throws IOException, OAuthException {
        String     body    = response.getBody();
        JSONObject json    = new JSONObject(body);
        int        errcode = json.getInt("errcode");
        if (errcode != 0) {
            throw new OAuthException(String.format("get access_token fail, body={}", body));
        }
        String accessToken = json.getString("access_token");
        int    expiresIn   = json.getInt("expires_in");

        return new OAuth2AccessToken(accessToken, null, expiresIn, null, null, body);
    }
}
