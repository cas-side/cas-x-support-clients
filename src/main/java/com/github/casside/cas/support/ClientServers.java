package com.github.casside.cas.support;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

public class ClientServers {

    private Map<String, ClientServer> servers = Maps.newHashMap();

    public void put(String code, ClientServer clientServer) {
        servers.put(code, clientServer);
    }

    public ClientServer get(String code) {
        return servers.get(code);
    }

    @Accessors(chain = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class ClientServer {

        /**
         * service name
         */
        String name;

        /**
         * visit URL
         */
        String url;

    }

}
