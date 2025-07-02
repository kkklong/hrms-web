package com.hrm.application.util;

import com.vaadin.flow.server.StreamResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FileDownloadUtil {

    // GET request StreamData
    public static StreamResource getFile(String url, Map<String, Object> pathValues,
                                              Map<String, Object> queryParams, String fileName) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        URI uri = (pathValues != null)
                ? builder.buildAndExpand(pathValues).toUri()
                : builder.build().toUri();
        Map<String, Object> headers = new HashMap<>();
        headers.put("Cookie", "JSESSIONID=" + SessionUtil.getToken());

        StreamResource resource = new StreamResource(fileName + "班表.xlsx", () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
                connection.setRequestProperty("Cookie", "JSESSIONID=" + SessionUtil.getToken());
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return new ByteArrayInputStream(new byte[0]);
            }
        });
        return resource;
    }
}
