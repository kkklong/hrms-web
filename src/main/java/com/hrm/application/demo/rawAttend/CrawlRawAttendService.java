package com.hrm.application.demo.rawAttend;

import com.hrm.application.entity.ApiResponse;
import com.hrm.application.entity.RawAttendanceRecords;
import com.hrm.application.util.WebClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CrawlRawAttendService {

    private final WebClientUtil client;


    static String HRM_OFFICIAL_URL = "http://192.168.0.75:8080";
//    static String HRM_OFFICIAL_URL = "http://hrms.tri-soaring.com.tw:8080";

    static String QUERY_RAW_ATTENDANCE_API = "/rawAttendanceRecords/query";
    static String LOGIN_API = "/account/login";


    public CrawlRawAttendService(WebClientUtil client) {
        this.client = client;
    }

    public List<RawAttendanceRecords> doFetchData(LocalDateTime startDate, LocalDateTime endDate, String account, Boolean showDetail) {
        String url = HRM_OFFICIAL_URL + QUERY_RAW_ATTENDANCE_API;
        String token = getToken();
        Map<String, Object> headers = new HashMap<>();
        System.out.println("Token: " + token);
        if(token.isEmpty()) {
            System.out.println("---- Token: GGGGGGGGGGGGGGG ----");
            return new ArrayList<>();
        }
        headers.put("Cookie", "JSESSIONID=" + token);
        ApiResponse<List<RawAttendanceRecords>> response = client.doGet(
                url,
                headers,
                null,
                queryParams(startDate, endDate, account, showDetail),
                new ParameterizedTypeReference<>() {}

        );
        if (response != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    private Map<String, Object> queryParams(LocalDateTime startDate, LocalDateTime endDate, String account, Boolean showDetail) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("startDate", startDate);
        queryParams.put("endDate", endDate);
        queryParams.put("account", account);
        queryParams.put("showDetail", showDetail);
        return queryParams;
    }

    private String getToken() {
        String url = HRM_OFFICIAL_URL + LOGIN_API;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("account", "rony");
        requestBody.put("password", "123456");

        ApiResponse<Map<String, String>> response = client.doPostJson(
                url,
                null,
                null,
                requestBody,
                new ParameterizedTypeReference<>() {}
        );
        String accessToken = "";
        if (response != null) {
            if (response.getCode().equals(0)) {
                Map<String, String> data = response.getData();
                accessToken = data.get("accessToken");
                return accessToken;
            }
        }
        return "";
    }

    private Boolean updateRawAttendence(List<RawAttendanceRecords> rawDatas) {
        String url = HRM_OFFICIAL_URL + LOGIN_API;

        ApiResponse<Map<String, String>> response = client.doPostJson(
                url,
                null,
                null,
                null,
                new ParameterizedTypeReference<>() {}
        );
        if(response != null) {
            return response.getCode() == 0 ? true : false;
        }
        return false;
    }
}
