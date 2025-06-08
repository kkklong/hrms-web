package com.hrm.application.service;

import com.hrm.application.config.BackendConfig;
import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.Menu;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.WebClientUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MenuService {

    @Value("${hrm.url}")
    private String backEndDomain;

    public List<Menu> getMenuList() {
        String url = backEndDomain + API.GET_MENU.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<List<Menu>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Menu>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                SessionUtil.cleanSession();
                NotificationUtil.success(response.getMessage());
                for (Menu menu : response.getData()) {
                    log.info("MenuData: "+ menu.toString());
                }
                return response.getData();
            }
        }
        NotificationUtil.error(response.getMessage());
        return null;
    }

    private enum API {

        GET_MENU("/menu", HttpMethod.GET, null),
        NONE("", null, null);


        String path;
        HttpMethod method;
        MediaType type;

        API(String path, HttpMethod method, MediaType type) {
            this.path = path;
            this.method = method;
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        public MediaType getType() {
            return type;
        }

        public void setType(MediaType type) {
            this.type = type;
        }

    }
}
