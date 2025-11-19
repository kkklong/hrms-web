package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.LeaveSpecialRecord;
import com.hrm.application.model.Option;
import com.hrm.application.model.vo.LeaveSpecialRecordsVO2;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaveTypeService {
    @Value("${hrm.url}")
    private String backEndDomain;

    private final BEClientRestUtil client;

    public LeaveTypeService(BEClientRestUtil client) {
        this.client = client;
    }

    public boolean saveLeaveSpecialRecords(LeaveSpecialRecord leaveSpecialRecord) {
        String url = backEndDomain + API.SAVE_LEAVE_SPECIAL_RECORD.getPath();
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<Object> response = client.doPostJson(url, null, leaveSpecialRecord, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
        NotificationUtil.error(response.getMessage());
        return false;
    }

    public boolean deleteLeaveSpecialRecords(Integer id) {
        String url = backEndDomain + API.DELETE_LEAVE_SPECIAL_RECORD.getPath();
        ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
        };
        Map<String, Object> pathValues = new HashMap<>();
        pathValues.put("id", id);
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

    public List<LeaveSpecialRecord> getAllLeaveSpecialRecords() {
        String url = backEndDomain + API.GET_LEAVE_SPECIAL_RECORDS.getPath();
        ParameterizedTypeReference<ApiResponse<List<LeaveSpecialRecord>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<LeaveSpecialRecord>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public List<LeaveSpecialRecord> queryCurrentLeaveSpecialRecordList() {
        String url = backEndDomain + API.GET_CURRENT_LEAVE_SPECIAL_RECORDS.getPath();
        ParameterizedTypeReference<ApiResponse<List<LeaveSpecialRecord>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<LeaveSpecialRecord>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public List<Option<String>> getNonAutoScheduledLeaves() {
        String url = backEndDomain + API.GET_NON_SCHEDULED_LEAVE_OPTIONS.getPath();
        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<String>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
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

    public List<Option<String>> getLeaveTypeSelectList() {
        String url = backEndDomain + API.GET_LEAVE_TYPE_OPTIONS.getPath();
        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<String>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public List<Option<Integer>> getDepartmentOptionList() {
        String url = backEndDomain + API.GET_DEPARTMENT_OPTIONS.getPath();
        ParameterizedTypeReference<ApiResponse<List<Option<Integer>>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<Option<Integer>>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

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

    public List<LeaveSpecialRecordsVO2> queryCurrentLeaveSpecialRecordList2() {
        String url = backEndDomain + API.GET_CURRENT_LEAVE_SPECIAL_RECORDS2.getPath();
        ParameterizedTypeReference<ApiResponse<List<LeaveSpecialRecordsVO2>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<LeaveSpecialRecordsVO2>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }


    private enum API {
        // 假別設定
        SAVE_LEAVE_SPECIAL_RECORD("/leaveSpecialRecords/saveLeaveSpecialRecords", HttpMethod.POST, MediaType.APPLICATION_JSON),
        MANUALLY_CREATE_LEAVE_SPECIAL_RECORDS("/leaveSpecialRecords/saveLeaveSpecialRecordsByScheduled/{year}", HttpMethod.POST, null),
        DELETE_LEAVE_SPECIAL_RECORD("/leaveSpecialRecords/delete/{id}", HttpMethod.POST, null),
        GET_LEAVE_SPECIAL_RECORDS("/leaveSpecialRecords/getAll", HttpMethod.GET, null),
        GET_CURRENT_LEAVE_SPECIAL_RECORDS("/leaveSpecialRecords/currentEmployeeLeaveSpecialRecords", HttpMethod.GET, null),
        GET_CURRENT_LEAVE_SPECIAL_RECORDS2("/leaveSpecialRecords/currentEmployeeLeaveSpecialRecords2", HttpMethod.GET, null),

        // enum
        GET_NON_SCHEDULED_LEAVE_OPTIONS("/enum/getNonAutoScheduledLeaveType", HttpMethod.GET, null),
        GET_SALARY_STANDARD_OPTIONS("/enum/getLeaveSpecialRecordSalaryStandard", HttpMethod.GET, null),


        GET_LEAVE_TYPE_OPTIONS("/enum/getLeaveType", HttpMethod.GET, null),
        // department
        GET_DEPARTMENT_OPTIONS("/department/getEnumList", HttpMethod.GET, null),
        // employee
        GET_EMPLOYEE_OPTIONS("/employee/getEnumList", HttpMethod.GET, null),


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
