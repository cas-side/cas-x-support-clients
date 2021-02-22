package com.github.casside.cas.support.qywx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.scribejava.core.model.OAuth2AccessToken;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

@Slf4j
@AllArgsConstructor
public class QyWxProfileDefinition extends OAuth20ProfileDefinition<OAuth20Profile, OAuth20Configuration> {

    /**
     * 指定要收集的属性集合
     */
    private final Map<String, String> profileAttributes = new HashMap<>();

    private UserProfileService userService;

    @Override
    public String getProfileUrl(OAuth2AccessToken accessToken, OAuth20Configuration configuration) {
        return ((QyWxApi) configuration.getApi()).getProfileUrl();
    }

    /**
     * 提取user profile
     */
    @Override
    public OAuth20Profile extractUserProfile(String body) {

        final OAuth20Profile profile = new OAuth20Profile();
        try {
            JSONObject userJson = JSONObject.parseObject(body);
            if (userJson != null) {
                String qyWxUserId = ProfileHelper.sanitizeIdentifier(profile, userJson.getString("UserId"));

                // 留下扩展点，当你需要别的地方查询额外的用户信息时，自定义 UserProfileService
                Map<String, Object> userEntity = userService.get(qyWxUserId);
                if (log.isDebugEnabled()) {
                    log.debug(String.format("qywx user related to: %s", JSON.toJSONString(userEntity)));
                }
                userJson.putAll(userEntity);

                if (userJson.get(getProfileId()) == null) {
                    throw new AuthenticationException("profile not found");
                }
                profile.setId(userJson.get(getProfileId()).toString());
                userJson.remove(getProfileId());

                this.profileAttributes.forEach((k, newKey) -> {
                    Object profileValue = userJson.get(k);
                    profile.addAttribute(newKey, profileValue);
                });

            } else {
                raiseProfileExtractionJsonError(body);
            }
        } catch (Exception e) {
            // 这个异常捕捉不要去掉，否则出错CAS不会显示具体错误信息
            log.error(e.getMessage(), e);
            throw new AuthenticationException(e.getMessage());
        }
        return profile;
    }

    public void addProfileAttribute(String key, String value) {
        profileAttributes.put(key, value);
    }
}
