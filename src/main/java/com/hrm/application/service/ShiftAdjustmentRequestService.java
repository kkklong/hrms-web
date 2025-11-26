package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.model.Option;
import com.hrm.application.model.vo.ShiftAdjustmentRequestVO;
import com.hrm.application.util.BEClientRestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftAdjustmentRequestService {

    @Value("${hrm.url}")
    private String backEndDomain;

    private final BEClientRestUtil client;

    public ShiftAdjustmentRequestService(BEClientRestUtil client) {
        this.client = client;
    }

    public List<ShiftAdjustmentRequestVO> getPendingShiftAdjustments() {
        String url = backEndDomain + API.GET_PENDING_SHIFT_ADJUSTMENTS.getPath();

        ParameterizedTypeReference<ApiResponse<List<ShiftAdjustmentRequestVO>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<ShiftAdjustmentRequestVO>> response = client.doGet(url, null, null, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }



    // ---- MenuOption ----
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

    // 公司下拉
//    public List<Option<String>> getCompanyOptionsList() {
//        String url = backEndDomain + API.GET_COMPANY_TYPE_OPTIONS.getPath();
//        ParameterizedTypeReference<ApiResponse<List<Option<String>>>> responseType = new ParameterizedTypeReference<>() {
//        };
//        ApiResponse<List<Option<String>>> response = client.doPostJson(url, null, null, responseType);
//        if (response != null) {
//            return response.getData();
//        }
//        return new ArrayList<>();
//    }

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


    private enum API {
        SHIFT_ADJUSTMENT_APPLY("/shiftAdjustmentRequest/apply", HttpMethod.POST, MediaType.APPLICATION_JSON),
        GET_PENDING_SHIFT_ADJUSTMENTS("/shiftAdjustmentRequest/getPendingShiftAdjustments", HttpMethod.GET, null),
        // 檔案
        DOWNLOAD_SAVE_ATTENDANCE_TEMPLATE("/sample/loadAttendanceRecordSample.xlsx", HttpMethod.GET, null),

        GET_DEPARTMENT_OPTIONS("/department/getEnumList", HttpMethod.GET, null),
        GET_EMPLOYEE_OPTIONS("/employee/getEnumList", HttpMethod.GET, null),
//        GET_COMPANY_TYPE_OPTIONS("/enum/getCompanyType", HttpMethod.GET, null),


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
