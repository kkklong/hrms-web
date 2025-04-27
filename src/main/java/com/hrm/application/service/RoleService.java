package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.Option;
import com.hrm.application.entity.Role;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.NotificationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RoleService {

    @Value("${hrm.url}")
    private String backEndDomain;

    protected List<Role> getAll() {
        String url = backEndDomain + API.GET_ROLES.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<List<Role>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Role>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    protected List<Option<Integer>> getRoleEnumList() {
        String url = backEndDomain + API.GET_ROLE_OPTIONS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    protected Role getById(Integer id) {
        String url = backEndDomain + API.QUERY_ROLE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        Map<String, Object> pathValues = new LinkedHashMap<>();
        pathValues.put("id", id);
        ParameterizedTypeReference<ApiResponse<Role>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Role> response = client.doGet(url, pathValues, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return null;
    }

    protected boolean save(Role role) {
        String url = backEndDomain + API.CREATE_ROLE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url, role, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    protected boolean updateRole(Role role) {
        String url = backEndDomain + API.UPDATE_ROLE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url, role, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    protected boolean delete(Role role) {
        String url = backEndDomain + API.DELETE_ROLE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        Map<String, Object> pathValues = new LinkedHashMap<>();
        pathValues.put("id", role.getId());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url, role, responseType);
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

        CREATE_ROLE("/role/create", HttpMethod.POST, MediaType.APPLICATION_JSON),
        UPDATE_ROLE("/role/update", HttpMethod.POST, MediaType.APPLICATION_JSON),
        DELETE_ROLE("/role/delete/{id}", HttpMethod.POST, null),
        GET_ROLES("/role/query", HttpMethod.GET, null),
        QUERY_ROLE("/role/query/{id}", HttpMethod.GET, null),
        GET_ROLE_OPTIONS("/role/getEnumList", HttpMethod.GET, null),

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
