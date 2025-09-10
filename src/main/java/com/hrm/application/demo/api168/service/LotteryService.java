package com.hrm.application.demo.api168.service;

import com.hrm.application.demo.api168.bean.LotteryData;
import com.hrm.application.demo.api168.bean.LotteryResponse;
import com.hrm.application.util.SessionUtil;
import com.hrm.application.util.WebClientUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LotteryService {

    private final WebClientUtil client;

    public LotteryService(WebClientUtil client) {
        this.client = client;
    }

    public List<LotteryData> fetchLotteryResult() {
        String url = "https://api.api168168.com/pks/getLotteryPksInfo.do?issue=&lotCode=10037";

        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", HttpMethod.GET);
        headers.put("Accept", "application/json");

        LotteryResponse response = client.doGet(
                url,
                headers,
                null,
                null,
                new ParameterizedTypeReference<LotteryResponse>() {}
        );

        if (response != null && response.getResult() != null && response.getResult().getData() != null) {
            return Collections.singletonList(response.getResult().getData());
        }
        return Collections.emptyList();
    }
}
