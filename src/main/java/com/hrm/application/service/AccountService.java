package com.hrm.application.service;

import com.hrm.application.config.BackendConfig;
import com.hrm.application.entity.ApiResponse;
import com.hrm.application.util.WebClientUtil;
import jakarta.annotation.Resource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class AccountService {
    @Resource
    BackendConfig backendConfig;


    public String login(String username, String password) {
        String url = backendConfig.getBackendDomain() + API.LOGIN.getPath();
        WebClientUtil client = new WebClientUtil(WebClient.builder().build());

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("account", username);
        requestBody.put("password", password);

        ParameterizedTypeReference<ApiResponse<Map<String, String>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Map<String, String>> response = client.doPostJson(url, null, requestBody, responseType);
        String accessToken = "";
        if (response != null) {
            Map<String, String> data = response.getData();
            accessToken = data.get("accessToken");
        }
        return accessToken;
    }


//    public boolean logout() {
//        boolean isSuccess = apiService.logout();
//        if (isSuccess) {
//            //session清除目前使用者
//            SessionUtil.cleanSession();
//        }
//        return isSuccess;
//    }

//    public boolean checkIsLogin() {
//        String token = SessionUtil.getToken();
//        UserInfo currentEmployee = SessionUtil.getUserInfo();
//        if (token == null) {
//            return false;
//        }
//        if (currentEmployee == null) {
//            return getCurrentUser() != null;
//        }
//        return true;
//    }

//    public UserInfo getCurrentUser() {
//        UserInfo userInfo = apiService.getCurrentUserInfo();
//        boolean isSuccess = userInfo != null;
//        if (isSuccess) {
//            SessionUtil.setUserInfo(userInfo);
//        }
//        return userInfo;
//    }

//    public boolean updatePassword(UpdatePassword updatePassword) {
//        return apiService.updatePassword(updatePassword);
//    }
//
//    public boolean updateUserInfo(UserInfo userInfo) {
//        return apiService.updateUserInfo(userInfo);
//    }

    private enum API {

        LOGIN("/account/login", HttpMethod.POST, MediaType.APPLICATION_JSON),
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
