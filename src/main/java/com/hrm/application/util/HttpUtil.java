package com.hrm.application.util;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Map;

public class HttpUtil {

    private final RestTemplate restTemplate;

    public HttpUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // GET 請求
    public <T> T get(String url, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    // POST 請求
    public <T, R> R post(String url, T requestBody, HttpHeaders headers, Class<R> responseType) {
        HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        return response.getBody();
    }
}
