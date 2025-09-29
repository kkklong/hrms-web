package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.UpdatePassword;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.util.BEClientRestUtil;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AccountService {

    @Value("${hrm.url}")
    private String backEndDomain;

    private final BEClientRestUtil client;
    public AccountService(BEClientRestUtil client) {
        this.client = client;
    }

    public String login(String username, String password) {
        String url = backEndDomain + API.LOGIN.getPath();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("account", username);
        requestBody.put("password", password);

        ParameterizedTypeReference<ApiResponse<Map<String, String>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Map<String, String>> response = client.doPostJson(url,null, requestBody, responseType);
        String accessToken = "";
        if (response != null) {
            if (response.getCode().equals(0)) {
                Map<String, String> data = response.getData();
                NotificationUtil.success(response.getMessage());
                accessToken = data.get("accessToken");
                SessionUtil.setToken(accessToken);
                return accessToken;
            }
        }
        NotificationUtil.error(response.getMessage());
        return accessToken;
    }

    public boolean logout() {
        String url = backEndDomain + API.LOGOUT.getPath();

        ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<String> response = client.doPostJson(url,null, null, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                SessionUtil.cleanSession();
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean checkIsLogin() {
        String token = SessionUtil.getToken();
        UserInfo currentEmployee = SessionUtil.getUserInfo();
        if (token == null) {
            return false;
        }
        if (currentEmployee == null) {
            NotificationUtil.error("用戶未登入");
            return false;
        }
        return true;
    }

    public UserInfo getCurrentUser() {
        UserInfo userInfo = getCurrentUserInfo();
        boolean isSuccess = userInfo != null;
        if (isSuccess) {
            SessionUtil.setUserInfo(userInfo);
        }
        return userInfo;
    }

    // 獲取當前用戶資訊
    protected UserInfo getCurrentUserInfo() {
        String url = backEndDomain + API.GET_CURRENT_USER.getPath();

        ParameterizedTypeReference<ApiResponse<UserInfo>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<UserInfo> response = client.doGet(url, null, null, responseType);

        if (response != null) {
            if (response.getCode().equals(0)) {
                return response.getData();
            }
        }
        NotificationUtil.error(response.getMessage());
        return null;
    }

    public boolean updatePassword(UpdatePassword updatePassword) {
        String url = backEndDomain + API.UPDATE_PASSWORD;

        ParameterizedTypeReference<ApiResponse<UpdatePassword>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<UpdatePassword> response = client.doPostJson(url, null, updatePassword, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean updateUserInfo(UserInfo userInfo) {
        String url = backEndDomain + API.UPDATE_USER_INFO;

        ParameterizedTypeReference<ApiResponse<UpdatePassword>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<UpdatePassword> response = client.doPostJson(url, null, userInfo, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }


    private enum API {

        LOGIN("/account/login", HttpMethod.POST, MediaType.APPLICATION_JSON),
        LOGOUT("/account/logout", HttpMethod.POST, null),
        GET_CURRENT_USER("/account/currentEmployee", HttpMethod.GET, null),
        UPDATE_PASSWORD("/account/updatePassword", HttpMethod.POST, MediaType.APPLICATION_JSON),
        UPDATE_USER_INFO("/account/updateUserInfo", HttpMethod.POST, MediaType.APPLICATION_JSON),

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
