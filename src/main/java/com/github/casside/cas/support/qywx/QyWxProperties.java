package com.github.casside.cas.support.qywx;

import com.github.casside.cas.support.ClientServer;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apereo.cas.configuration.model.support.pac4j.oauth.Pac4jOAuth20ClientProperties;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class QyWxProperties extends Pac4jOAuth20ClientProperties {

    private Map<String, ClientServer> clients;

    public QyWxProperties() {
        this.setCustomParams(new HashMap<>());
    }

    /**
     * agentid
     */
    public void setAgentid(String agentid) {
        this.getCustomParams().put("agentid", agentid);
    }


}
