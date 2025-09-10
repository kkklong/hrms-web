package com.hrm.application.demo.sport.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.application.demo.api168.bean.LotteryData;
import com.hrm.application.demo.api168.bean.LotteryResponse;
import com.hrm.application.util.WebClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SportService {
    private final WebClientUtil client;
    static String PORXY_URL = "https://m806.mos011.com/transform.php";
    static String PORXY_HOST = "m806.mos011.com";
    static String MEMBER_URL = "https://m806.mos011.com/";
    static String MEMBER_HOST = "https://m806.mos011.com";
    //版本
    static String VER = "2025-09-10-c1bug_115";
    static String LOCAL_LINK = "D:/accounts.txt";
    public SportService(WebClientUtil client) {
        this.client = client;

    }
    public List fetchResult() {
        String url = PORXY_URL + "?ver=" + VER;
        String response = client.doPostForm(
                url,
                setHeader(PORXY_HOST),
                null,
                setGameListParam(),
                new ParameterizedTypeReference<String>() {}
        );
        if (response != null) {
            System.out.println("ResponseData: " + response);
        }
        return Collections.emptyList();
    }

    public List getMemberLogin() {
        String url = MEMBER_URL;

        String response = client.doPostForm(
                url,
                setHeader(MEMBER_HOST),
                memberLogin("TCPF78", "04TFWN", "2025-09-10-c1bug_115"),
                null,
                new ParameterizedTypeReference<String>() {}
        );

        if (response != null) {
            String dataNode = response;
            System.out.println(dataNode);

        }
        return Collections.emptyList();

    }

    public static Map<String, Object> memberLogin(String username, String password,
                                                            String version) {
        Map<String, Object> map = new HashMap<>();
        map.put("p", "chk_login");
        map.put("langx", "zh-cn");
        map.put("ver", version);
        map.put("username", username);
        map.put("password", password);
        map.put("app", "N");
        map.put("auto", "IAGBGH");
        map.put("blackbox", "");
        return map;
    }

    private static Map<String, Object> setHeader(String host) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Accept-encoding", "gzip, deflate, br, zstd");
        headers.put("Accept-language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7,zh-CN;q=0.6");
        headers.put("Connection", "keep-alive");
        headers.put("content-length", "212");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
        headers.put("Content-type", "application/x-www-form-urlencoded");
        headers.put("Cookie", "LPVID=a3lORE01T0dWak1UYzJZakUxTkROaA==; protocolstr=aHR0cHM=; test=aW5pdA; box4pwd_notshow_38922868=Mzg5MjI4NjhfTg==; CookieChk=WQ; iorChgSw=WQ==; myGameVer_38922868=XzIxMTIyOA==; ft_myGame_38922868=e30=; bk_myGame_38922868=e30=; login_38922868=MTc1NzU0NTcyMA; cu=Tg==; cuipv6=Tg==; ipv6=Tg==");
        headers.put("Host", host);
        headers.put("Origin", MEMBER_HOST);
        headers.put("sec-ch-ua", "Chromium\";v=\"140\", \"Not=A?Brand\";v=\"24\", \"Google Chrome\";v=\"140\"");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("sec-ch-ua-platform", "Windows");
        headers.put("Sec-Fetch-Dest", "empty");
        headers.put("Sec-Fetch-Mode", "cors");
        headers.put("Sec-Fetch-Site", "same-origin");
//        headers.put("Referer", MEMBER_HOST);
        return headers;
    }

    private static Map<String, Object> setGameListParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("uid", "s0kp9fj9pm38922868l11403b0");
        param.put("ver", "2025-09-10-c1bug_115");
        param.put("langx", "zh-tw");
        param.put("p", "get_game_list");
        param.put("p3type", "");
        param.put("date", "");
        param.put("gtype", "ft");
        param.put("ltype", "4");
        param.put("filter", "");
        param.put("cupFantasy", "N");
        param.put("sorttype", "L");
        param.put("specialClick", "");
        param.put("isFantasy", "N");
        param.put("ts", String.valueOf(System.currentTimeMillis()));
        return param;
    }

}
