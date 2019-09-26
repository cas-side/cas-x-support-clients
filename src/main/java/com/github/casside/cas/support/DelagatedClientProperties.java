package com.github.casside.cas.support;

import com.github.casside.cas.support.qywx.QyWxProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ConfigurationProperties(prefix = "cas-x.authn")
public class DelagatedClientProperties {

    QyWxProperties qyWx;

}
