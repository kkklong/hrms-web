package com.hrm.application.demo.payment;

import com.hrm.application.util.WebClientUtil;
import org.springframework.core.ParameterizedTypeReference;

import java.util.HashMap;
import java.util.Map;

public class ThirdPartyService {

    private final WebClientUtil client;

    static String ASYCNALLBACK_URL = "http://test77web.javawebdata5.com";
    static String ASYNAPI = "/api/recharge/onlinePayAsyncCallback/";
    public ThirdPartyService(WebClientUtil client) {
        this.client = client;
    }


    public String doAsyncCallback() {
        String orderNo = "20251030004351497238";
        String url = ASYCNALLBACK_URL + ASYNAPI + orderNo;
        String response = client.doPostForm(
                url,
                null,
                null,
                setAsynParam(),
                new ParameterizedTypeReference<String>() {}
        );
        return response;
    }

    private static Map<String, Object> setAsynParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("ifCode", "dashi");
        param.put("createdAt", String.valueOf(System.currentTimeMillis()));
        param.put("payOrderId", "P1928000302412279810");
        param.put("mchOrderNo", "20251030004351497238");
        param.put("clientIp", "127.0.0.1");
        param.put("successTime", String.valueOf(System.currentTimeMillis()));
        param.put("sign", "1152A2725FAA414BD2DF5A009F7BD611");
        param.put("state", "2");
        param.put("reqTime", "N");
        param.put("mchNo", "M1746863481");

        return param;
    }

}
