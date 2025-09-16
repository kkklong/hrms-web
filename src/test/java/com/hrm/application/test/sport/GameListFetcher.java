package com.hrm.application.test.sport;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GameListFetcher {

    public static void sendGameListRequest(String uid) throws IOException {
        String url = "https://m806.mos011.com/transform.php";
        String ts = String.valueOf(System.currentTimeMillis());
        String base64TS = Base64.getEncoder().encodeToString(ts.getBytes(StandardCharsets.UTF_8));
        // 表單參數（URL 編碼）
        String urlParameters = "p=game_list_FT" +
//                "&ver=2025-09-10-c1bug_115" +
                "&langx=zh-tw" +
                "&uid=" + URLEncoder.encode(uid, "UTF-8") +
                "&ts=" + URLEncoder.encode(ts, "UTF-8") +
                "&gtype=ft" +
                "&showtype=live" +
                "&rtype=rb";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // Headers
//        conn.setRequestProperty("Accept", "*/*");
//        conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7");
//        conn.setRequestProperty("Cache-Control", "no-cache");
//        conn.setRequestProperty("Connection", "keep-alive");
//        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        conn.setRequestProperty("Origin", "https://m806.mos011.com");
//        conn.setRequestProperty("Pragma", "no-cache");
//        conn.setRequestProperty("Sec-Fetch-Dest", "empty");
//        conn.setRequestProperty("Sec-Fetch-Mode", "cors");
//        conn.setRequestProperty("Sec-Fetch-Site", "same-origin");
//        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1");
//        conn.setRequestProperty("Cookie", "CookieChk=WQ; LPVID=MyODQwMDE1YzEyMDFiYjRi; box4pwd_notshow_38734694=Mzg3MzQ2OTRfTg==; box4pwd_notshow_38785783=Mzg3ODU3ODNfTg==; iorChgSw=WQ==; myGameVer_38785783=XzIxMTIyOA==; tn_myGame_38785783=e30=; protocolstr=aHR0cHM=; test=aW5pdA; box4pwd_notshow_38923813=Mzg5MjM4MTNfTg==; myGameVer_38923813=XzIxMTIyOA==; ft_myGame_38923813=e30=; login_38923813="+base64TS+"; cu=Tg==; cuipv6=Tg==; ipv6=Tg==; loadBB=b2s=; login_38923813="+base64TS+"; test=aW5pdA");
//        conn.setRequestProperty("Cookie", "login_38923813="+base64TS);

        // 寫入 POST body
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(urlParameters.getBytes(StandardCharsets.UTF_8));
        }

        // 讀取回應
        int status = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder responseContent = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            responseContent.append(inputLine);
        }
        in.close();

        // 輸出回應
        System.out.println("Status: " + status);
        System.out.println("Response: " + responseContent);
    }

    public static void sendGameListRequest2(String uid) throws IOException {
        String url = "https://m806.mos011.com/transform.php?ver=2025-09-10-c1bug_115";
        String ts = String.valueOf(System.currentTimeMillis());

        // POST body（raw 格式）
        String body =
                "uid=" + URLEncoder.encode(uid, "UTF-8") +
                        "&ts=" + URLEncoder.encode(ts, "UTF-8") +
//                        "&ver=2025-09-10-c1bug_115" +
                        "&langx=zh-tw" +
                        "&p=get_game_list" +
                        "&p3type=" +
                        "&date=" +
                        "&gtype=bk" +
                        "&showtype=live" +
                        "&rtype=rb" +
                        "&ltype=4" +
                        "&filter=" +
                        "&cupFantasy=N" +
                        "&sorttype=L" +
                        "&specialClick=" +
                        "&isFantasy=N";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // 設定 headers
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Origin", "https://m806.mos011.com");
        conn.setRequestProperty("Pragma", "no-cache");
        conn.setRequestProperty("Sec-Fetch-Dest", "empty");
        conn.setRequestProperty("Sec-Fetch-Mode", "cors");
        conn.setRequestProperty("Sec-Fetch-Site", "same-origin");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1");

        // Cookie 設定
//        conn.setRequestProperty("Cookie", "CookieChk=WQ; LPVID=MyODQwMDE1YzEyMDFiYjRi; box4pwd_notshow_38734694=Mzg3MzQ2OTRfTg==; box4pwd_notshow_38785783=Mzg3ODU3ODNfTg==; iorChgSw=WQ==; myGameVer_38785783=XzIxMTIyOA==; tn_myGame_38785783=e30=; protocolstr=aHR0cHM=; test=aW5pdA; box4pwd_notshow_38923813=Mzg5MjM4MTNfTg==; myGameVer_38923813=XzIxMTIyOA==; ft_myGame_38923813=e30=; bk_myGame_38923813=e30=; login_38923813=MTc1NzU2NDc4OA; box4pwd_notshow_38923695=Mzg5MjM2OTVfTg==; login_38923695=MTc1NzU2NDg1NQ; myGameVer_38923695=XzIxMTIyOA==; cu=Tg==; cuipv6=Tg==; ipv6=Tg==");

        // 傳送 POST body
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(body.getBytes(StandardCharsets.UTF_8));
        }

        // 讀取 response
        int status = conn.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String line;
        StringBuilder responseBody = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            responseBody.append(line);
        }
        reader.close();

        // 輸出結果
        System.out.println("HTTP Status: " + status);
        System.out.println("Response Body: ");
        System.out.println(responseBody);
    }
}