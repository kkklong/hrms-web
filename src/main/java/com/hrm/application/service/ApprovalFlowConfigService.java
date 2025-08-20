package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.ApprovalFlowConfig;
import com.hrm.application.model.Option;
import com.hrm.application.util.BEClientRestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ApprovalFlowConfigService {

    @Value("${hrm.url}")
    private String backEndDomain;

    private final BEClientRestUtil client;
    public ApprovalFlowConfigService(BEClientRestUtil client) {
        this.client = client;
    }

    public List<ApprovalFlowConfig> queryApprovalFlowConfigs(String scopeType, Integer active) {
        String url = backEndDomain + API.APPROVAL_FLOW_CONFIG_QUERY.getPath();
        LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("scopeType", scopeType);
        queryParams.put("Integer", active);
        ParameterizedTypeReference<ApiResponse<List<ApprovalFlowConfig>>> type = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<ApprovalFlowConfig>> resp = client.doPostJson(url, null, queryParams, type);
        return resp != null && resp.getData() != null ? resp.getData() : new ArrayList<>();
    }


    // ---- 下拉選單List ----

    public List<Option<String>> getScopeTypeOptions() {
        String url = backEndDomain + API.APPROVAL_FLOW_CONFIG_SCOPE_TYPE_SELECTOR.getPath();

        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<String>>> resp = client.doPostJson(url, null, null, responseType);
        return resp != null && resp.getData() != null ? resp.getData() : new ArrayList<>();
    }

    // 審核人下拉
    public List<Option<String>> getReviewOptions() {
        String url = backEndDomain + API.APPROVAL_FLOW_CONFIG_REVIEW_SELECTOR.getPath();
        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> type = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<String>>> resp = client.doPostJson(url, null, Collections.emptyMap(), type);
        return resp != null && resp.getData() != null ? resp.getData() : new ArrayList<>();
    }

    // 審核區間下拉
    public List<Option<String>> getReviewIntervalOptions() {
        String url = backEndDomain + API.APPROVAL_FLOW_CONFIG_REVIEW_INTERVAL_SELECTOR.getPath();
        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> type = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<String>>> resp = client.doPostJson(url, null, Collections.emptyMap(), type);
        return resp != null && resp.getData() != null ? resp.getData() : new ArrayList<>();
    }

    // ---- department ----
    public List<Option<Integer>> getDepartmentOptionList() {
        String url = backEndDomain + API.GET_DEPARTMENT_OPTIONS.getPath();

        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doPostJson(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    // 公司下拉
    public List<Option<String>> getCompanyOptionsList() {
        String url = backEndDomain + API.APPROVAL_FLOW_CONFIG_COMPANY_SELECTOR.getPath();
        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<String>>> response = client.doPostJson(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    // 取得員工清單 下拉選單
    public List<Option<Integer>> getEmployeeOptionList() {
        String url = backEndDomain + API.GET_EMPLOYEE_OPTIONS.getPath();

        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }


    // enum
//    public List<Option<Integer>> getCompanyTypeOptionList() {
//        String url = backEndDomain + API.GET_COMPANY_TYPE_OPTIONS.getPath();
//        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
//        };
//        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
//        if (response != null) {
//            return response.getData();
//        }
//        return new ArrayList<>();
//    }


    private enum API {
        APPROVAL_FLOW_CONFIG_CREATE("/approvalFlowConfig/create", HttpMethod.POST, MediaType.APPLICATION_JSON),
        APPROVAL_FLOW_CONFIG_UPDATE("/approvalFlowConfig/update", HttpMethod.POST, MediaType.APPLICATION_JSON),
        APPROVAL_FLOW_CONFIG_QUERY("/approvalFlowConfig/query", HttpMethod.POST, MediaType.APPLICATION_JSON),
        APPROVAL_FLOW_CONFIG_DELETE("/approvalFlowConfig/delete/{id}", HttpMethod.POST, null),
        APPROVAL_FLOW_CONFIG_SCOPE_TYPE_SELECTOR("/approvalFlowConfig/scopeTypeSelector", HttpMethod.POST, MediaType.APPLICATION_JSON),
        APPROVAL_FLOW_CONFIG_REVIEW_SELECTOR("/approvalFlowConfig/reviewSelector", HttpMethod.POST, MediaType.APPLICATION_JSON),
        APPROVAL_FLOW_CONFIG_REVIEW_INTERVAL_SELECTOR("/approvalFlowConfig/reviewIntervalSelector", HttpMethod.POST, MediaType.APPLICATION_JSON),
        APPROVAL_FLOW_CONFIG_COMPANY_SELECTOR("/approvalFlowConfig/companySelector", HttpMethod.POST, MediaType.APPLICATION_JSON),

        GET_DEPARTMENT_OPTIONS("/department/getEnumList", HttpMethod.GET, null),
        GET_EMPLOYEE_OPTIONS("/employee/getEnumList", HttpMethod.GET, null),
        GET_COMPANY_TYPE_OPTIONS("/enum/getCompanyType", HttpMethod.GET, null),


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
