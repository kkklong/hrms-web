package com.hrm.application.util;
import com.vaadin.flow.component.UI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class BEClientRestUtil {

    private final RestTemplate restTemplate;

    public BEClientRestUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    // JSON 請求
    public <T> T doPostJson(String url, Map<String, Object> pathValues, Object jsonBody, ParameterizedTypeReference<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = (pathValues != null)
                ? builder.buildAndExpand(pathValues).toUri()
                : builder.build().toUri();
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", HttpMethod.POST);
        headers.put("Cookie", "JSESSIONID=" + SessionUtil.getToken());
        return requestData(uri, HttpMethod.POST, MediaType.APPLICATION_JSON, headers, jsonBody, null, responseType);
    }

    // FORM 請求
    public <T> T doPostForm(String url, Map<String, Object> pathValues, Map<String, Object> formBody, ParameterizedTypeReference<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = (pathValues != null)
                ? builder.buildAndExpand(pathValues).toUri()
                : builder.build().toUri();
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", HttpMethod.POST);
        headers.put("Cookie", "JSESSIONID=" + SessionUtil.getToken());
        return requestData(uri, HttpMethod.POST, MediaType.APPLICATION_FORM_URLENCODED, headers, null, formBody, responseType);
    }


    // GET 請求
    public <T> T doGet(String url, Map<String, Object> pathValues,
                       Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        URI uri = (pathValues != null)
                ? builder.buildAndExpand(pathValues).toUri()
                : builder.build().toUri();
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", HttpMethod.GET);
        headers.put("Cookie", "JSESSIONID=" + SessionUtil.getToken());
        return requestData(uri, HttpMethod.GET, MediaType.APPLICATION_JSON, headers, null, null, responseType);
    }

    private <T> T requestData(URI uri, HttpMethod method, MediaType mediaType,
                              Map<String, Object> headers, Object jsonBody, Map<String, Object> formBody, ParameterizedTypeReference<T> responseType) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType);
        if (headers != null) {
            headers.forEach((key, value) -> {
                if (key.equalsIgnoreCase(HttpHeaders.COOKIE) && value instanceof String) {
                    httpHeaders.add(HttpHeaders.COOKIE, (String) value);
                } else {
                    httpHeaders.set(key, value.toString());
                }
            });
        }

        // 設定 body（依 mediaType 決定是 JSON 或 FORM）
        Object body = null;
        if (mediaType == MediaType.APPLICATION_JSON && jsonBody != null) {
            body = jsonBody;
        } else if (mediaType == MediaType.APPLICATION_FORM_URLENCODED && formBody != null) {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formBody.forEach((key, value) -> formData.add(key, value.toString()));
            body = formData;
        }

        HttpEntity<?> entity = new HttpEntity<>(body, httpHeaders);

        try {
            log.info("request uri: {}, method: {}, type: {},  body: {}", uri, method, mediaType, body);
            ResponseEntity<T> response = restTemplate.exchange(uri, method, entity, responseType);
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            handleHttpError(ex);
            throw new RuntimeException("HTTP request failed: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            log.error("Error: request URI: {}; msg: {}", uri, e.getMessage());
            throw new RuntimeException("Unexpected error occurred during HTTP request", e);
        }
    }

    /**
     * 處理未登入
     */
    private void handleHttpError(HttpStatusCodeException e) {

        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            SessionUtil.cleanSession();
            // Redirect to login page
            UI.getCurrent().getPage().setLocation("/login");
        }
    }
}
