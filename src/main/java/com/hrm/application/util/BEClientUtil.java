package com.hrm.application.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.application.service.AccountService;
import com.vaadin.flow.component.UI;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BEClientUtil {

    @Value("${hrm.url}")
    private String apiDomain;

    @Resource
    ObjectMapper objectMapper;

    private final WebClient webClient;

    public BEClientUtil(WebClient webClient) {
        this.webClient = webClient;
    }

    // JSON 請求
    public <T> T doPostJson(String url, Object jsonBody, ParameterizedTypeReference<T> responseType) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).build().toUri();
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", HttpMethod.POST);
        headers.put("Cookie", "JSESSIONID=" + SessionUtil.getToken());
        return requestData(uri, HttpMethod.POST, MediaType.APPLICATION_JSON, headers, jsonBody, null, responseType);
    }

    // FORM 請求
    public <T> T doPostForm(String url, Map<String, Object> formBody, ParameterizedTypeReference<T> responseType) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).build().toUri();
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", HttpMethod.POST);
        headers.put("Cookie", "JSESSIONID=" + SessionUtil.getToken());
        return requestData(uri, HttpMethod.POST, MediaType.APPLICATION_FORM_URLENCODED, headers, null, formBody, responseType);
    }

    // GET 請求
    public <T> T doGet(String url, Map<String, Object> pathValues,
                       Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (pathValues != null) {
            builder.buildAndExpand(pathValues).toUri();
        }
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        URI uri = builder.build().toUri();
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", HttpMethod.GET);
        headers.put("Cookie", "JSESSIONID=" + SessionUtil.getToken());
        return requestData(uri, HttpMethod.GET, MediaType.APPLICATION_JSON, headers, null, null, responseType);
    }


    private <T> T requestData(URI uri, HttpMethod method, MediaType mediaType,
                              Map<String, Object> headers, Object jsonBody, Map<String, Object> formBody, ParameterizedTypeReference<T> responseType) {

        WebClient.RequestBodySpec requestSpec = webClient
                .method(method)
                .uri(uri)
                .contentType(mediaType);

        // 設置 Headers
        if (headers != null) {
            headers.forEach((key, value) -> requestSpec.header(key, String.valueOf(value)));
        }

        // 處理 JSON 或 Form 參數
        if (mediaType.equals(MediaType.APPLICATION_JSON) && jsonBody != null) {
            requestSpec.bodyValue(jsonBody);
        } else if (mediaType.equals(MediaType.APPLICATION_FORM_URLENCODED) && formBody != null) {
            requestSpec.bodyValue(formBody);
        }

        T response = null;
        try {
            response = requestSpec.exchangeToMono(resp -> {
                HttpStatusCode status = resp.statusCode();
                log.info("Request to URL: {}, HTTP Status: {}", uri, status);
                return resp.bodyToMono(responseType);
            }).block();
        } catch (HttpClientErrorException e) {  // HTTP status code 為 4xx、5xx
            log.error("ClientError: request URI: {}; HTTP error: {}; {} - status ; Header: {};", uri, e.getStatusCode(), e.getMessage(), headers);
            NotificationUtil.error(e.getStatusCode().toString() +"-"+ e.getMessage());
            handleHttpError(e);

            throw e;
        } catch (Exception e) {
            NotificationUtil.error(e.getMessage());
            log.error("Error: request URI: {}; msg: {}; Header: {};", uri, e.getMessage(), headers);
        }

        return response;
    }

    /**
     * 處理未登入
     */
    private void handleHttpError(HttpClientErrorException e) {

        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            SessionUtil.cleanSession();
            // Redirect to login page
            UI.getCurrent().getPage().setLocation("/login");
        }
    }
}
