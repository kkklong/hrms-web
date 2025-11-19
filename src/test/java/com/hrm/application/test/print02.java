package com.hrm.application.test;

import com.hrm.application.demo.payment.ThirdPartyService;
import com.hrm.application.demo.rawAttend.CrawlRawAttendService;
import com.hrm.application.demo.sport.service.SportService;
import com.hrm.application.entity.RawAttendanceRecords;
import com.hrm.application.service.RawAttendanceRecordsQueryService;
import com.hrm.application.util.BEClientRestUtil;
import com.hrm.application.util.EncryptionUtils;
import com.hrm.application.util.JsonUtils;
import com.hrm.application.util.WebClientUtil;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class print02 {


    @Test
    public void Test0001(){
        String gameId = "205";
        String turnNum = "2025274";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 256; i++) {
            sb.append("DELETE FROM user_bet_")
                    .append(i)
                    .append(" WHERE game_id = '")
                    .append(gameId)
                    .append("' AND turn_num = '")
                    .append(turnNum)
                    .append("'");
            if (i < 255) { // 最後一個不用加 UNION ALL
                sb.append("\nUNION ALL\n");
            }
        }

        System.out.println(sb.toString());
    }

    @Test
    public void Test0002(){
        String addTime = "2025-08-01";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 256; i++) {
            sb.append("DELETE FROM user_bill_")
                    .append(i)
                    .append(" WHERE add_time < '")
                    .append(addTime)
                    .append("'")
                    .append(";\n");
//            if (i < 255) { // 最後一個不用加 UNION ALL
//                sb.append("\nUNION ALL\n");
//            }
        }

        System.out.println(sb.toString());
    }

    @Test
    public void Test0003(){
        String s = "1519\n" +
                "1agm\n" +
                "1ayl\n" +
                "1gyh\n" +
                "1jlc\n" +
                "1mpj\n" +
                "1yfc\n" +
                "2cyh\n" +
                "2eyh\n" +
                "2gyh\n" +
                "2hgf\n" +
                "2hgh\n" +
                "2lpj\n" +
                "33cp\n" +
                "3eyh\n" +
                "3gyh\n" +
                "3kws\n" +
                "3mgm\n" +
                "5000\n" +
                "5bjs\n" +
                "5fjs\n" +
                "5jpj\n" +
                "5lpj\n" +
                "63cp\n" +
                "66cp\n" +
                "6ajs\n" +
                "6dpj\n" +
                "6hgc\n" +
                "6lpj\n" +
                "7cpj\n" +
                "7djs\n" +
                "7lpj\n" +
                "7mpj\n" +
                "888c\n" +
                "8bjs\n" +
                "8cyh\n" +
                "8dws\n" +
                "8gws\n" +
                "8hpj\n" +
                "8lws\n" +
                "9jws\n" +
                "9lpj\n" +
                "aofa\n" +
                "awan\n" +
                "betdc\n" +
                "bwzz\n" +
                "cai7\n" +
                "cai8\n" +
                "cjs\n" +
                "cmpj\n" +
                "cpj\n" +
                "dmpj\n" +
                "hgwa\n" +
                "hgwb\n" +
                "hhws\n" +
                "jinc\n" +
                "msmh\n" +
                "vtyc\n" +
                "ymgm\n" +
                "zwnx\n";

        String s1 = s.replace(" ","\":\"").replace("\n","\",\"");
        //  String s2 = s1.replace("\n","\'\n");
        System.out.printf(s1);
    }

    @Test
    public void TestAsynCallBack(){
        WebClient.Builder builder = WebClient.builder();
        WebClientUtil client = new WebClientUtil(builder);

        ThirdPartyService service = new ThirdPartyService(client);

        String resp = service.doAsyncCallback();
        System.out.println("Response: " + resp);

    }

    @Test
    public void TestGetRawAttendanceRecord(){
        WebClient.Builder builder = WebClient.builder();
        WebClientUtil client = new WebClientUtil(builder);

        CrawlRawAttendService service = new CrawlRawAttendService(client);
        int year = 2025;
        int month = 9;
        LocalDate selectedDate = LocalDate.of(year, month, 1);

        LocalDateTime startDate = LocalDateTime.parse(selectedDate.atStartOfDay().toString());
        LocalDateTime endDate = LocalDateTime.parse(selectedDate.plusMonths(1).atStartOfDay().minusSeconds(1).toString());
        String account = "";
        Boolean showDetail = false;

        List<RawAttendanceRecords> resp = service.doFetchData(startDate, endDate, account, showDetail);
        System.out.println("Response: " + resp.toString());

//        RestTemplate restTemplate = new RestTemplate();
//        BEClientRestUtil client2 = new BEClientRestUtil(restTemplate);
//
//        RawAttendanceRecordsQueryService service2 = new RawAttendanceRecordsQueryService(client2);
//        boolean result = service2.updateData(resp);
//        System.out.println("紀錄更新: " + result );
    }

    @Test
    public void sign() throws NoSuchAlgorithmException {
        Map<String, Object> params = new HashMap<>();
        params.put("merchantUserId", "8gws_agtest8899");
        params.put("platForm", "pc");
        params.put("ip","210.68.242.61");
        String merchantId = "1690274264809746433";
        String requestTimestamp = "1763394325153";
        String merchantApiSecret = "roFnUTk6peRtCpXYygtAFGKKIbZ2mmEV";
        LinkedHashMap<String, Object> map = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        String paramsJson = JsonUtils.toJSON(map);
        String signString = String.format("%s.%s.%s.%s", paramsJson, merchantId, requestTimestamp, merchantApiSecret);

        System.out.println(EncryptionUtils.encryptWithMD5(signString));
    }
}
