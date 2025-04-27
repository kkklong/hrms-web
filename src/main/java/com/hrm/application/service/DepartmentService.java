package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.Department;
import com.hrm.application.entity.Option;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.NotificationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class DepartmentService {

    @Value("${hrm.url}")
    private String backEndDomain;

    // 查詢部門資料
    protected List<Department> getAll() {
        String url = backEndDomain + API.GET_DEPARTMENTS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<List<Department>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Department>> response = client.doGet(url, null, null, responseType);
        List<Department> result = new ArrayList<>();
        if (response != null) {
            return response.getData();
        }
        return result;
    }

    public List<Option<Integer>> getOptionList() {
        String url = backEndDomain + API.GET_DEPARTMENTS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    protected boolean save(Department department) {
        String url = backEndDomain + API.CREATE_DEPARTMENT.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url, department, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    protected boolean updateDepartment(Department department) {
        String url = backEndDomain + API.UPDATE_DEPARTMENT.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url, department, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    protected boolean delete(Department department) {
        String url = backEndDomain + API.DELETE_DEPARTMENT.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url, department, responseType);
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

        GET_DEPARTMENTS("/department/query", HttpMethod.GET, null),
        GET_DEPARTMENT_OPTIONS("/department/getEnumList", HttpMethod.GET, null),
        CREATE_DEPARTMENT("/department/create", HttpMethod.POST, MediaType.APPLICATION_JSON),
        UPDATE_DEPARTMENT("/department/update", HttpMethod.POST, MediaType.APPLICATION_JSON),
        DELETE_DEPARTMENT("/department/delete/{id}", HttpMethod.POST, null),


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

