package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.Employee;
import com.hrm.application.entity.Option;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.NotificationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    @Value("${hrm.url}")
    private String backEndDomain;

    // 獲取所有員工列表 (權限limit)
    protected List<Employee> getEmployeeList() {
        String url = backEndDomain + API.GET_EMPLOYEES.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<List<Employee>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Employee>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    // 取得員工清單 下拉選單
    protected List<Option<Integer>> getEmployeeOptionList() {
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

    // 獲取特定員工資訊
    protected Employee getEmployeeById(Integer id) {
        String url = backEndDomain + API.QUERY_EMPLOYEE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        Map<String, Object> pathValues = new LinkedHashMap<>();
        pathValues.put("id", id);
        ParameterizedTypeReference<ApiResponse<Employee>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Employee> response = client.doGet(url, pathValues, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return null;
    }

    // 根據部門ID查詢該部門員工
    protected List<Employee> getEmployeeListByDepartmentId(Integer departmentId) {
        String url = backEndDomain + API.QUERY_EMPLOYEES_IN_DEPARTMENT.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        Map<String, Object> pathValues = new LinkedHashMap<>();
        pathValues.put("departmentId", departmentId);
        ParameterizedTypeReference<ApiResponse<List<Employee>>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<List<Employee>> response = client.doGet(url, pathValues, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    // 新增員工資料
    protected boolean createEmployee(Employee employee) {
        String url = backEndDomain + API.CREATE_EMPLOYEE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<Employee>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Employee> response = client.doPostJson(url, employee, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    // 修改員工資料
    protected boolean updateEmployee(Employee employee) {
        String url = backEndDomain + API.UPDATE_EMPLOYEE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<Employee>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Employee> response = client.doPostJson(url, employee, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    // 刪除員工資料
    protected boolean deleteEmployee(Employee employee) {
        String url = backEndDomain + API.DELETE_EMPLOYEE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<Employee>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Employee> response = client.doPostJson(url, employee, responseType);
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

        GET_EMPLOYEES("/employee/getAllEmployee", HttpMethod.GET, null),
        GET_EMPLOYEE_OPTIONS("/employee/getEnumList", HttpMethod.GET, null),
        QUERY_EMPLOYEE("/employee/query/{id}", HttpMethod.GET, null),
        QUERY_EMPLOYEES_IN_DEPARTMENT("/employee/queryByDepartmentId/{departmentId}", HttpMethod.GET, null),
        CREATE_EMPLOYEE("/employee/create", HttpMethod.POST, MediaType.APPLICATION_JSON),
        UPDATE_EMPLOYEE("/employee/update", HttpMethod.POST, MediaType.APPLICATION_JSON),
        DELETE_EMPLOYEE("/employee/delete/{id}", HttpMethod.POST, null),

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
