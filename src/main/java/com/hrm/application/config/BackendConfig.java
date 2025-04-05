package com.hrm.application.config;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BackendConfig {

    private String backendDomain;

    public String getBackendDomain() {
        return "http://localhost:8080/";
    }

    public Map<String, Object> getHeaders(Object accessToken) {
        return Map.of("Cookie", "JSESSIONID=" + accessToken);
    }
}
