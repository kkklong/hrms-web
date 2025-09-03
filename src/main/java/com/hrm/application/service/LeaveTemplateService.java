package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.LeaveSpecialRecordTemplate;
import com.hrm.application.model.Option;
import com.hrm.application.util.BEClientRestUtil;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.NotificationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaveTemplateService {

    @Value("${hrm.url}")
    private String backEndDomain;

    private final BEClientRestUtil client;

    public LeaveTemplateService(BEClientRestUtil client) {
        this.client = client;
    }

    public List<LeaveSpecialRecordTemplate> queryLeaveTemplate() {
        String url = backEndDomain + API.GET_LEAVE_TEMPLATES.getPath();
        ParameterizedTypeReference<ApiResponse<List<LeaveSpecialRecordTemplate>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<LeaveSpecialRecordTemplate>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public boolean updateLeaveTemplate(LeaveSpecialRecordTemplate leaveSpecialRecordTemplate) {
        String url = backEndDomain + API.UPDATE_LEAVE_TEMPLATE.getPath();
        ParameterizedTypeReference<ApiResponse<LeaveSpecialRecordTemplate>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<LeaveSpecialRecordTemplate> response = client.doPostJson(url, null, leaveSpecialRecordTemplate, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public List<Option<String>> querySalaryStandard() {
        String url = backEndDomain + API.GET_SALARY_STANDARD_OPTIONS.getPath();
        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<String>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public List<Option<String>> queryCalculationPeriod() {
        String url = backEndDomain + API.GET_CALCULATION_PERIOD_OPTIONS.getPath();
        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<String>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }




    public String getLeaveName(LeaveSpecialRecordTemplate template) {
        return template.getChineseName() + (template.getYearData() != null ? formatYearData(template.getYearData()) : "");
    }

    private String formatYearData(Float yearData) {
        if (yearData == 0.5) {
            return "(半年)";
        } else if (yearData >= 1) {
            return "(" + (int) Math.floor(yearData) + "年)";
        } else {
            return ""; // yearData = 0 或其他< 1 的非 0.5 值，返回空字串
        }
    }

    private enum API {

        // 預設假別設定
        GET_LEAVE_TEMPLATES("/leaveSpecialRecordsTemplate/queryLeaveSpecialRecordsTemplate", HttpMethod.GET, null),
        UPDATE_LEAVE_TEMPLATE("/leaveSpecialRecordsTemplate/updateLeaveSpecialRecordsTemplate", HttpMethod.POST, MediaType.APPLICATION_JSON),
        // ---- enum ----
        GET_SALARY_STANDARD_OPTIONS("/enum/getLeaveSpecialRecordSalaryStandard", HttpMethod.GET, null),
        GET_CALCULATION_PERIOD_OPTIONS("/enum/getLeaveSpecialRecordCalculationPeriod", HttpMethod.GET, null),


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
