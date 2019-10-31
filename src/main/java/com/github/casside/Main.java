package com.github.casside;

import com.alibaba.fastjson.JSON;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {

        String url = "https://sso.example.com:8000/login?service=https://localhost:8080/shiro-cas";
        url = url.split("\\?")[1];

        Map<String, List<String>> map = URLUtils.parseParameters(url);
        System.out.println(JSON.toJSONString(map));
    }

}
