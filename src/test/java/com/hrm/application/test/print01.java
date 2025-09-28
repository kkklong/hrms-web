package com.hrm.application.test;

import com.hrm.application.demo.sport.service.SportService;
import com.hrm.application.util.SportsHttp;
import com.hrm.application.util.WebClientUtil;
import com.hrm.application.util.XMLUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class print01 {


    @Test
    public void test01() {
        System.out.println("flowConfig: ");
        System.out.println("reviewSelector: [{\"name\":\"組長審核\",\"value\":\"LEADER_REVIEW\",\"referenceValue\":null},{\"name\":\"人資審核\",\"value\":\"HR_REVIEW\",\"referenceValue\":null},{\"name\":\"技術長審核\",\"value\":\"TECH_LEAD_REVIEW\",\"referenceValue\":null},{\"name\":\"總經理審核\",\"value\":\"GM_REVIEW\",\"referenceValue\":null}]");
        System.out.println("reviewIntervalSelector: [{\"name\":\"大於等於24小時\",\"value\":\"GE_24\",\"referenceValue\":null},{\"name\":\"小於24小時\",\"value\":\"LT_24\",\"referenceValue\":null}]");
    }

    @Test
    public void Iptest() throws IOException {
        String ip = Jsoup.connect("https://api.ipify.org").ignoreContentType(true).execute().body();
        System.out.println(ip);
    }

    @Test
    public void SportTest() throws IOException {
//        String ip = Jsoup.connect("https://api.ipify.org").ignoreContentType(true).execute().body();
//        System.out.println("IP: "+ip);
        // 手動 new WebClientUtil
        WebClient.Builder builder = WebClient.builder();
        WebClientUtil client = new WebClientUtil(builder);

        SportService service = new SportService(client);
        String xmlResponse = service.fetchResult();
        System.out.println("ResponseData: " + xmlResponse);

        System.out.println("jsonData: " + XMLUtils.xml2json(xmlResponse));
    }

    @Test
    public void SportTest2() throws Exception {
        String VER = "2025-09-10-c1bug_115";
        String url = "https://m806.mos011.com/transform.php" + "?ver=" + VER;

        Header[] headers = new Header[]{
                new BasicHeader("Accept", "*/*"),
                new BasicHeader("Accept-encoding", "gzip, deflate, br"),
//                new BasicHeader("Accept-language", "zh-TW,zhH-Hant;q=0.9"),
//                new BasicHeader("Connection", "keep-alive"),
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded"),
//                new BasicHeader("Content-length", "211"),
//                new BasicHeader("Cookie", "myGameVer_38657565=XzIxMTIyOA==; test=aW5pdA; cu=Tg==; cuipv6=Tg==; ipv6=Tg==; iorChgSw=WQ==; protocolstr=aHR0cHM=; CookieChk=WQ; login_38657565=MTc1ODA0NzA2Nw; box4pwd_notshow_38657565=Mzg2NTc1NjVfTg==; myGameVer_38922868=XzIxMTIyOA==; ft_myGame_38922868=e30=; login_38922868=MTc1ODA0NjMzMg; box4pwd_notshow_38922868=Mzg5MjI4NjhfTg=="),
//                new BasicHeader("User-Agent",
//                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4.1 Safari/605.1.15"),
//                new BasicHeader("Content-type", "application/x-www-form-urlencoded"),
//                new BasicHeader("Host", "m806.mos011.com"),
//                new BasicHeader("Origin", "https://m806.mos011.com"),
//                new BasicHeader("Sec-Fetch-Dest", "empty"),
//                new BasicHeader("Sec-Fetch-Mode", "cors"),
//                new BasicHeader("Sec-Fetch-Site", "same-origin")
        };
        Map<String, Object> params = new HashMap<>();
        params.put("uid", "9uvnzhu9m38657565l73980b0");
        params.put("ver", "2025-09-10-c1bug_115");
        params.put("langx", "zh-tw");
        params.put("p", "get_game_list");
//        params.put("p3type", "");
//        params.put("date", "");
        params.put("gtype", "ft");
        params.put("showtype", "live");
        params.put("rtype", "rb");
        params.put("ltype", "4");
//        params.put("filter", "");
        params.put("cupFantasy", "N");
        params.put("sorttype", "L");
//        params.put("specialClick", "");
        params.put("isFantasy", "N");
        params.put("ts", String.valueOf(System.currentTimeMillis()));

        String response = SportsHttp.doPost(url, params, headers);
        log.info("[{}] get data from api url:{} params:{} response:\n{}"
                , "Rb get_list"
                , url
                , params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"))
                , response);
    }

    @Test
    public void SportLoginTest() throws Exception {
        String VER = "2025-09-10-c1bug_115";
        String url = "https://m806.mos011.com/transform.php" + "?ver=" + VER;

        Header[] headers = new Header[]{
                new BasicHeader("Accept", "*/*"),
                new BasicHeader("Accept-encoding", "gzip, deflate, br"),
//                new BasicHeader("Accept-language", "zh-TW,zhH-Hant;q=0.9"),
                new BasicHeader("Connection", "keep-alive"),
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded"),
                new BasicHeader("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4.1 Safari/605.1.15")
        };
        Map<String, Object> params = new HashMap<>();
        params.put("p", "chk_login");
        params.put("langx", "zh-cn");
        params.put("username", "Dt7172");
        params.put("password", "Dtv7172");
        params.put("ver", "2025-09-10-c1bug_115");
        params.put("auto", "IAIHGD");
        params.put("app", "N");
        params.put("userAgent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4.1 Safari/605.1.15");

        String response = SportsHttp.doPost(url, params, headers);
//        log.info("[{}] get data from api url:{} params:{} response:\n{}"
//                , "Rb get_list"
//                , url
//                , params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"))
//                , response);

        log.info("jsonData: " + XMLUtils.xml2json(response));
    }

}
