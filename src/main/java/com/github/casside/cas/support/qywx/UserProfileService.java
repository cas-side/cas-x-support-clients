package com.github.casside.cas.support.qywx;

import java.util.Map;

/**
 * 企业微信profile服务
 *
 * 当你需要从数据库查询其他字段的时候，重写 {@link #get(String)} 方法
 */
public interface UserProfileService {

    /**
     * @param qyWxUserId 企业微信 UserId
     * @return 企业微信 user profile
     */
    Map<String, Object> get(String qyWxUserId);
}
