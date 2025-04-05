package com.hrm.application.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class WebClientUtil {

    private final WebClient webClient;

    public WebClientUtil(WebClient webClient) {
        this.webClient = webClient;
    }

    // JSON 請求
    public <T> T doPostJson(String url, Map<String, Object> headers, Object jsonBody, ParameterizedTypeReference<T> responseType) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).build().toUri();
        return requestData(uri, HttpMethod.POST, MediaType.APPLICATION_JSON, headers, jsonBody, null, responseType);
    }

    // FORM 請求
    public <T> T doPostForm(String url, Map<String, Object> headers, Map<String, Object> formBody, ParameterizedTypeReference<T> responseType) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).build().toUri();
        return requestData(uri, HttpMethod.POST, MediaType.APPLICATION_FORM_URLENCODED, headers, null, formBody, responseType);
    }

    // GET 請求
    public <T> T doGet(String url, Map<String, Object> headers, Map<String, Object> pathValues,
                       Map<String, Object> queryParams,ParameterizedTypeReference<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (pathValues != null) {
            builder.buildAndExpand(pathValues).toUri();
        }
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        URI uri = builder.build().toUri();
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
            String errorMsg = "HTTP error: " + e.getStatusCode() + " - " + e.getStatusText();
            log.error(errorMsg);
            throw e;
        } catch (Exception e) {
            String errorMsg = "error occurred: " + e.getMessage();
            log.error(errorMsg, e);
        }

        return response;
    }
}
