package com.hrm.application.service;

import com.hrm.application.config.BackendConfig;
import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.ShiftSchedules;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.WebClientUtil;
import jakarta.annotation.Resource;
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
public class ShiftScheduleService {

    @Value("${hrm.url}")
    private String backEndDomain;

    //查詢班表
    public List<ShiftSchedules> queryShiftSchedules(String startDate, String endDate, Integer departmentId) {
        String url = backEndDomain + API.QUERY_SHIFT_SCHEDULES.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("startDate", startDate);
        queryParams.put("endDate", endDate);
        queryParams.put("departmentId", departmentId);

        ParameterizedTypeReference<ApiResponse<List<ShiftSchedules>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<ShiftSchedules>> response = client.doGet(url, null, queryParams, responseType);
        if (response != null && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    //查詢班別資訊
    public List<ShiftType> getShiftAndHolidayConfigList() {
        String url = backEndDomain + API.GET_SHIFT_HOLIDAY_TYPES.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<List<ShiftType>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<ShiftType>> response = client.doGet(url, null, null, responseType);
        if (response != null && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    /**
     * 接口信息
     */
    private enum API {

        GET_SHIFT_HOLIDAY_TYPES("/shiftSchedules/getShiftAndHolidayConfig", HttpMethod.GET, null),
        QUERY_SHIFT_SCHEDULES("/shiftSchedules/queryByMonthAndDepartment", HttpMethod.GET, null),
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
