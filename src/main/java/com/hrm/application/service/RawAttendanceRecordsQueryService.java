package com.hrm.application.service;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.RawAttendanceRecords;
import com.hrm.application.model.bo.UpdateAttendanceRequest;
import com.hrm.application.util.BEClientRestUtil;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.NotificationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class RawAttendanceRecordsQueryService {

    @Value("${hrm.url}")
    private String backEndDomain;

    private final BEClientRestUtil client;

    public RawAttendanceRecordsQueryService(BEClientRestUtil client) {
        this.client = client;
    }

    public List<RawAttendanceRecords> getRawAttendanceRecords(LocalDateTime startDate, LocalDateTime endDate, String account, Boolean showDetail) {
        String url = backEndDomain + API.QUERY_PUNCH_RECORDS.getPath();

        ParameterizedTypeReference<ApiResponse<List<RawAttendanceRecords>>> responseType = new ParameterizedTypeReference<>() {
        };
        LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("startDate", startDate);
        queryParams.put("endDate", endDate);
        queryParams.put("account", account);
        queryParams.put("showDetail", showDetail);

        ApiResponse<List<RawAttendanceRecords>> response = client.doGet(url, null, queryParams, responseType);
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public boolean updateData(UpdateAttendanceRequest items) {
        String url = backEndDomain + API.UPDATE_PUNCH_RECORDS.getPath();
        BEClientUtil client = new BEClientUtil(WebClient.builder().build());

        ParameterizedTypeReference<ApiResponse<List<RawAttendanceRecords>>> responseType = new ParameterizedTypeReference<>() {
        };
        ApiResponse<List<RawAttendanceRecords>> response = client.doPostJson(url,null, items, responseType);
        if (response != null) {
            if (response.getCode().equals(0)) {
//                NotificationUtil.success(response.getMessage());
                return true;
            }
        }
//        NotificationUtil.error(response.getMessage());
        return false;
    }


    private enum API {
        QUERY_PUNCH_RECORDS("/rawAttendanceRecords/query", HttpMethod.GET, null),
        UPDATE_PUNCH_RECORDS("/rawAttendanceRecords/update" , HttpMethod.POST, null),


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
