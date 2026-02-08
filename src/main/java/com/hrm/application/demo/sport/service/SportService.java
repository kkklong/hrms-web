package com.hrm.application.demo.sport.service;

import com.hrm.application.util.WebClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

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
    static String VER = "2025-09-30-updateBanner_118";
    static String LOCAL_LINK = "D:/accounts.txt";
    public SportService(WebClientUtil client) {
        this.client = client;

    }
    public String fetchResult() {
//        String url = PORXY_URL + "?ver=" + VER;
        String url = PORXY_URL;
        String xmlResponse = client.doPostForm(
                url,
                setListFTHeader(PORXY_HOST),
                null,
                setGameListParam(),
                new ParameterizedTypeReference<String>() {}
        );
        if (xmlResponse != null) {
            return  xmlResponse;
//            System.out.println("ResponseData: " + response);
        }
        return "GG";
    }

    public String gameMore() {
//        String url = PORXY_URL + "?ver=" + VER;
        String url = PORXY_URL;

        String xmlResponse = client.doPostForm(
                url,
                setHeader(PORXY_HOST),
                null,
                setFtGameMoreParam(),
                new ParameterizedTypeReference<String>() {}
        );
        if (xmlResponse != null) {
            return  xmlResponse;
//            System.out.println("ResponseData: " + response);
        }
        return "GG";
    }

    public String gameMore2() {
//        String url = PORXY_URL + "?ver=" + VER;
        String url = PORXY_URL;

        String xmlResponse = client.doPostForm(
                url,
                setHeader(PORXY_HOST),
                null,
                setBkGameMoreParam(),
                new ParameterizedTypeReference<String>() {}
        );
        if (xmlResponse != null) {
            return  xmlResponse;
//            System.out.println("ResponseData: " + response);
        }
        return "GG";
    }

    public String parlayData() {
//        String url = PORXY_URL + "?ver=" + VER;
        String url = PORXY_URL;
        String xmlResponse = client.doPostForm(
                url,
                setListFTHeader(PORXY_HOST),
                null,
                setParlayParam(),
                new ParameterizedTypeReference<String>() {}
        );
        if (xmlResponse != null) {
            return  xmlResponse;
//            System.out.println("ResponseData: " + response);
        }
        return "GG";
    }

    public String fuData() {
//        String url = PORXY_URL + "?ver=" + VER;
        String url = PORXY_URL;
        String xmlResponse = client.doPostForm(
                url,
                setListFBKFUHeader(PORXY_HOST),
                null,
                setBKFUParam(),
                new ParameterizedTypeReference<String>() {}
        );
        if (xmlResponse != null) {
            return  xmlResponse;
//            System.out.println("ResponseData: " + response);
        }
        return "GG";
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
//        headers.put("Accept", "*/*");
//        headers.put("Accept-encoding", "gzip, deflate, br, zstd");
//        headers.put("Accept-language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7,zh-CN;q=0.6");
//        headers.put("Connection", "keep-alive");
//        headers.put("content-length", "132");
//        headers.put("User-Agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
//        headers.put("Content-type", "application/x-www-form-urlencoded");
//        headers.put("Cookie", "test=aW5pdA; myGameVer_38922868=XzIxMTIyOA==; login_38922868=MTc1Nzc4MDM3NQ; cu=Tg==; cuipv6=Tg==; ipv6=Tg==; iorChgSw=WQ==; protocolstr=aHR0cHM=; CookieChk=WQ; box4pwd_notshow_38922868=Mzg5MjI4NjhfTg==");
//        headers.put("Host", host);
//        headers.put("Origin", MEMBER_HOST);
//        headers.put("sec-ch-ua", "Chromium\";v=\"140\", \"Not=A?Brand\";v=\"24\", \"Google Chrome\";v=\"140\"");
////        headers.put("sec-ch-ua-mobile", "?0");
////        headers.put("sec-ch-ua-platform", "Windows");
//        headers.put("Sec-Fetch-Dest", "empty");
//        headers.put("Sec-Fetch-Mode", "cors");
//        headers.put("Sec-Fetch-Site", "same-origin");
//        headers.put("Referer", MEMBER_HOST);
        return headers;
    }

    private static Map<String, Object> setListFTHeader(String host) {
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Accept", "*/*");
//        headers.put("Accept-encoding", "gzip, deflate, br, zstd");
//        headers.put("Accept-language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7,zh-CN;q=0.6");
//        headers.put("Connection", "keep-alive");
//        headers.put("content-length", "213");
//        headers.put("User-Agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
//        headers.put("Content-type", "application/x-www-form-urlencoded");
        String cookieJson = "[{\"name\":\"Cookie\",\"value\":\"login_39224450=1760205409; test=init\",\"elements\":[{\"name\":\"login_39224450\",\"value\":\"1760205409\",\"parameters\":[{\"name\":\"test\",\"value\":\"init\"}],\"parameterCount\":1}]}]";
        headers.put("Cookie",cookieJson);
//        headers.put("Cookie", "myGameVer_38922868=XzIxMTIyOA==; test=aW5pdA; login_38922868=MTc1Nzc4MDQzNQ; cu=Tg==; cuipv6=Tg==; ipv6=Tg==; iorChgSw=WQ==; protocolstr=aHR0cHM=; CookieChk=WQ; box4pwd_notshow_38922868=Mzg5MjI4NjhfTg==");
//        headers.put("Host", host);
//        headers.put("Origin", MEMBER_HOST);
//        headers.put("sec-ch-ua", "Chromium\";v=\"140\", \"Not=A?Brand\";v=\"24\", \"Google Chrome\";v=\"140\"");
//        headers.put("sec-ch-ua-mobile", "?0");
//        headers.put("sec-ch-ua-platform", "Windows");
//        headers.put("Sec-Fetch-Dest", "empty");
//        headers.put("Sec-Fetch-Mode", "cors");
//        headers.put("Sec-Fetch-Site", "same-origin");
//        headers.put("Referer", MEMBER_HOST);
        return headers;
    }

    private static Map<String, Object> setListFBKFUHeader(String host) {
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Accept", "*/*");
//        headers.put("Accept-encoding", "gzip, deflate, br, zstd");
//        headers.put("Accept-language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7,zh-CN;q=0.6");
//        headers.put("Connection", "keep-alive");
//        headers.put("content-length", "213");
//        headers.put("User-Agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
//        headers.put("Content-type", "application/x-www-form-urlencoded");
        String cookieJson = "[{\"name\":\"Cookie\",\"value\":\"login_39224450=1760205409; test=init\",\"elements\":[{\"name\":\"login_39224450\",\"value\":\"1760205409\",\"parameters\":[{\"name\":\"test\",\"value\":\"init\"}],\"parameterCount\":1}]}]";
        headers.put("Cookie",cookieJson);
        headers.put("Cookie", "myGameVer_38922868=XzIxMTIyOA==; test=aW5pdA; login_38922868=MTc1Nzc4MDQzNQ; cu=Tg==; cuipv6=Tg==; ipv6=Tg==; iorChgSw=WQ==; protocolstr=aHR0cHM=; CookieChk=WQ; box4pwd_notshow_38922868=Mzg5MjI4NjhfTg==");
        headers.put("Host", host);
        headers.put("Origin", MEMBER_HOST);
        headers.put("sec-ch-ua", "Chromium\";v=\"140\", \"Not=A?Brand\";v=\"24\", \"Google Chrome\";v=\"140\"");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("sec-ch-ua-platform", "Windows");
        headers.put("Sec-Fetch-Dest", "empty");
        headers.put("Sec-Fetch-Mode", "cors");
        headers.put("Sec-Fetch-Site", "same-origin");
        headers.put("Referer", MEMBER_HOST);
        return headers;
    }

    private static Map<String, Object> setGameListParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("uid", "gp1pj19jrm38657565l31240b0");
        param.put("ver", null);
        param.put("langx", "zh-cn");
        param.put("p", "get_game_list");
//        param.put("p3type", "");
//        param.put("date", "");
        param.put("gtype", "ft");
        param.put("showtype", "live");
        param.put("rtype", "rb");
        param.put("ltype", "4");
//        param.put("filter", "");
        param.put("cupFantasy", "N");
        param.put("sorttype", "L");
//        param.put("specialClick", "");

        param.put("isFantasy", "N");
        param.put("ts", String.valueOf(System.currentTimeMillis()));
        return param;
    }

    private static Map<String, Object> setFtGameMoreParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("uid", "4gnai9o0drm39572137l440788b0");
        param.put("ver", null);
        param.put("langx", "zh-cn");
        param.put("p", "get_game_more");
        param.put("gtype", "ft");
        param.put("showtype", "today");
        param.put("ltype", "4");
        param.put("isRB", "N");
        param.put("lid", "103389");
        //        param.put("specialClick", "");
//        param.put("mode", "NORMAL");
        param.put("from", "game_more");
//        param.put("filter", "Main");
        param.put("ts", String.valueOf(System.currentTimeMillis()));
        param.put("ecid", "10211945");
        return param;
    }

    private static Map<String, Object> setBkGameMoreParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("uid", "k2cqsikkqm39572137l450186b0");
        param.put("ver", null);
        param.put("langx", "zh-cn");
        param.put("p", "get_game_more");
        param.put("gtype", "bk");
        param.put("showtype", "today");
        param.put("ltype", "4");
        param.put("isRB", "N");
        param.put("lid", "100828");
        //        param.put("specialClick", "");
//        param.put("mode", "NORMAL");
        param.put("from", "game_more");
//        param.put("filter", "Main");
        param.put("ts", String.valueOf(System.currentTimeMillis()));
        param.put("gid", "9997626");
        return param;
    }

    private static Map<String, Object> setParlayParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("uid", "6u5asw6wm38657565l43281b0");
        param.put("ver", null);
        param.put("langx", "zh-cn");
        param.put("p", "get_game_more");
        param.put("gtype", "ft");
        param.put("showtype", "today");
        param.put("ltype", "4");
        param.put("isRB", "N");
        param.put("lid", "101505");
        //        param.put("specialClick", "");
//        param.put("mode", "NORMAL");
        param.put("from", "game_more");
//        param.put("filter", "Main");
        param.put("ts", String.valueOf(System.currentTimeMillis()));
        param.put("ecid", "10011438");
        return param;
    }

    private static Map<String, Object> setBKFUParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("uid", "n79xuns1m39572137l379555b0");
        param.put("ver", "2025-12-26-noLog_129");
        param.put("langx", "zh-cn");
        param.put("p", "get_game_list");
        param.put("date", "all");
        param.put("gtype", "bk");
        param.put("showtype", "early");
        param.put("rtype", "r");
        param.put("ltype", "4");
        param.put("isRB", "N");
        param.put("filter", "FU");
        param.put("cupFantasy", "N");
        param.put("lid", "100879, 101458");
        param.put("action", "clickCoupon");
        param.put("sorttype", "T");
        param.put("isFantasy", "N");
        param.put("ts", String.valueOf(System.currentTimeMillis()));
        return param;
    }

}
