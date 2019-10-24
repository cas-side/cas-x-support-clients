package com.github.casside.cas.support.qywx;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class QrCodeJsConfig {

    String corpId;

    String agentId;
    /**
     * 重定向的URL
     */
    String redirectUri;
    /**
     * 微信回调时，保持一致
     *
     * 在cas委托登录中，必须存在，对应sts.id，大概是为了防止伪造登录吧
     */
    String state;

    String href;

}