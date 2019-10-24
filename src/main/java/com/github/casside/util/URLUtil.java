package com.github.casside.util;

import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.util.List;
import org.springframework.util.CollectionUtils;

public class URLUtil {

    public static String getParam(String url, String name) {
        String[] splits = url.split("\\?");
        if (splits.length > 0) {
            List<String> p = URLUtils.parseParameters(splits[1]).get(name);
            if (!CollectionUtils.isEmpty(p)) {
                return p.get(0);
            }
        }
        return null;
    }

}
