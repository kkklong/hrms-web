package com.hrm.application.test.sport;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {
        String url = "https://m806.mos011.com/transform.php?ver=2025-09-10-c1bug_115";
        UnsafeSSL.disableCertificateValidation();
        // 建立 POST body 的表單資料
        String urlParameters =
                "p=chk_login" +
                        "&langx=zh-tw" +
                        "&ver=2025-09-10-c1bug_115" +
                        "&username=3WNedv" +
                        "&password=TC8HFo" +
                        "&app=N" +
                        "&auto=IAGBGH" +
                        "&blackbox=" +
                        "&userAgent=" + URLEncoder.encode("TW96aWxsYS81LjAgKGlQaG9uZTsgQ1BVIGlQaG9uZSBPUyAxOF81IGxpa2UgTWFjIE9TIFgpIEFwcGxlV2ViS2l0LzYwNS4xLjE1IChLSFRNTCwgbGlrZSBHZWNrbykgVmVyc2lvbi8xOC41IE1vYmlsZS8xNUUxNDggU2FmYXJpLzYwNC4x", "UTF-8")
                ;

        // 建立連線
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // 設定 headers（模擬瀏覽器）
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
//        conn.setRequestProperty("Cookie", "CookieChk=WQ; LPVID=MyODQwMDE1YzEyMDFiYjRi; box4pwd_notshow_38734694=Mzg3MzQ2OTRfTg==; box4pwd_notshow_38785783=Mzg3ODU3ODNfTg==; iorChgSw=WQ==; myGameVer_38785783=XzIxMTIyOA==; tn_myGame_38785783=e30=; protocolstr=aHR0cHM=; test=aW5pdA; box4pwd_notshow_38923813=Mzg5MjM4MTNfTg==; login_38923813=MTc1NzU1NDQ1NA; myGameVer_38923813=XzIxMTIyOA==; ft_myGame_38923813=e30=; loadBB=WQ==; cu=Tg==; cuipv6=Tg==; ipv6=Tg==; login_38923813=MTc1NzU1NTA2Mg; test=aW5pdA");

        // 發送表單資料
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(urlParameters.getBytes(StandardCharsets.UTF_8));
        }

        // 讀取回應
        int status = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        // 關閉連線與 reader
        in.close();
        conn.disconnect();

        // 輸出回應內容
        System.out.println("HTTP Status: " + status);
        System.out.println("Response Body: ");
        System.out.println(content.toString());

        XmlMapper xmlMapper = new XmlMapper();
        ServerResponse responseObj = xmlMapper.readValue(content.toString(), ServerResponse.class);

        System.out.println("Parsed username: " + responseObj.getUsername());
        System.out.println("Parsed uid: " + responseObj.getUid());
        GameListFetcher.sendGameListRequest(responseObj.getUid());
    }
}