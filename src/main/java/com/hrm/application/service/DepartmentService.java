package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.Department;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.model.Option;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.NotificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Slf4j
@Service
public class DepartmentService {

    @Value("${hrm.url}")
    private String backEndDomain;

    // 查詢部門資料
    public List<Department> getAll() {
        String url = backEndDomain + API.GET_DEPARTMENTS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<List<Department>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Department>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public List<Option<Integer>> getOptionList() {
        String url = backEndDomain + API.GET_DEPARTMENT_OPTIONS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public boolean save(Department department) {
        String url = backEndDomain + API.CREATE_DEPARTMENT.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Object> response = client.doPostJson(url,null, department, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean update(Department department) {
        String url = backEndDomain + API.UPDATE_DEPARTMENT.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<Object> response = client.doPostJson(url,null, department, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean delete(Department department) {
        String url = backEndDomain + API.DELETE_DEPARTMENT.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        Map<String, Object> pathValues = new LinkedHashMap<>();
        pathValues.put("id", department.getId());
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Object> response = client.doPostJson(url, pathValues, null, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    // 取得員工清單 下拉選單
    public List<Option<Integer>> getEmployeeOptionList() {
        String url = backEndDomain + API.GET_EMPLOYEE_OPTIONS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    //查詢班別EnumList
    public List<Option<String>> getShiftTypeOptionList() {
        String url = backEndDomain + API.GET_SHIFT_TYPE_OPTIONS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<String>>> response = client.doGet(url, null, null, responseType);
        if (response != null && response.getData() != null) {
//            response.getData().forEach(option ->
//                    log.info("Option - Value: {}, Label: {}", option.getValue(), option.getName())
//            );
            return response.getData();
        }
        return new ArrayList<>();
    }


    private enum API {

        GET_DEPARTMENTS("/department/query", HttpMethod.GET, null),
        GET_DEPARTMENT_OPTIONS("/department/getEnumList", HttpMethod.GET, null),
        CREATE_DEPARTMENT("/department/create", HttpMethod.POST, MediaType.APPLICATION_JSON),
        UPDATE_DEPARTMENT("/department/update", HttpMethod.POST, MediaType.APPLICATION_JSON),
        DELETE_DEPARTMENT("/department/delete/{id}", HttpMethod.POST, null),

        // ---- enum ----
        GET_EMPLOYEE_OPTIONS("/employee/getEnumList", HttpMethod.GET, null),
        GET_SHIFT_TYPE_OPTIONS("/enum/getShiftType", HttpMethod.GET, null),




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

