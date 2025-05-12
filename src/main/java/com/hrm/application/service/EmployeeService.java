package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.Employee;
import com.hrm.application.model.Option;
import com.hrm.application.entity.ResetPassword;
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
public class EmployeeService {

    @Value("${hrm.url}")
    private String backEndDomain;

    // 獲取所有員工列表 (權限limit)
    public List<Employee> getEmployeeList() {
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

    // 獲取特定員工資訊
    public Employee getEmployeeById(Integer id) {
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
    public List<Employee> getEmployeeListByDepartmentId(Integer departmentId) {
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
    public boolean createEmployee(Employee employee) {
        String url = backEndDomain + API.CREATE_EMPLOYEE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<Employee>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Employee> response = client.doPostJson(url,null, employee, responseType);
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
    public boolean updateEmployee(Employee employee) {
        String url = backEndDomain + API.UPDATE_EMPLOYEE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<Employee>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Employee> response = client.doPostJson(url,null, employee, responseType);
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
    public boolean deleteEmployee(Employee employee) {
        String url = backEndDomain + API.DELETE_EMPLOYEE.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<Employee>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Employee> response = client.doPostJson(url,null, employee, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    //取得員工狀態列表
    public List<Option<Byte>> getEmployeeStatusOptionList() {
        String url = backEndDomain + API.GET_EMPLOYEE_STATUS_OPTIONS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<List<Option<Byte>>>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<List<Option<Byte>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    // ---- company ----
    public List<Option<Integer>> getCompanyTypeOptionList() {
        String url = backEndDomain + API.GET_COMPANY_TYPE_OPTIONS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    // ---- department ----
    public List<Option<Integer>> getDepartmentOptionList() {
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

    // ---- role ----
    public List<Option<Integer>> getRoleEnumList() {
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

    // ---- account ----
    public boolean resetPassword(ResetPassword resetPassword) {
        String url = backEndDomain + API.RESET_PASSWORD.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());
        ParameterizedTypeReference<ApiResponse<ResetPassword>> responseType = new ParameterizedTypeReference<>() {
        };

        ApiResponse<ResetPassword> response = client.doPostJson(url,null, resetPassword, responseType);
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
        // ---- employee enum ----
        GET_EMPLOYEE_STATUS_OPTIONS("/enum/getEmployeeStatus", HttpMethod.GET, null),
        // ---- company ----
        GET_COMPANY_TYPE_OPTIONS("/enum/getCompanyType", HttpMethod.GET, null),
        // ---- department ----
        GET_DEPARTMENT_OPTIONS("/department/getEnumList", HttpMethod.GET, null),
        // ---- role ----
        GET_ROLE_OPTIONS("/role/getEnumList", HttpMethod.GET, null),
        // ---- account ----
        RESET_PASSWORD("/account/resetPassword", HttpMethod.POST, MediaType.APPLICATION_JSON),



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
