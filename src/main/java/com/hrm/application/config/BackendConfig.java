package com.hrm.application.config;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BackendConfig {

    public static final String backendDomain = "http://localhost:8080/";

    public String getBackendDomain() {
        return backendDomain;
    }

    public Map<String, Object> getHeaders(Object accessToken) {
        return Map.of("Cookie", "JSESSIONID=" + accessToken);
    }
}
