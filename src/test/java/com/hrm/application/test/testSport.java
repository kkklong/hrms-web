package com.hrm.application.test;


import com.fasterxml.jackson.databind.JsonNode;
import com.hrm.application.util.XMLUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

@Slf4j
public class testSport {


    @Test
    public void BaseHandleTest() throws IOException, JSONException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/java/com/hrm/application/test/sport/BK_R.json"));
        String xml = new String(bytes, StandardCharsets.UTF_8);
//        System.out.println(xml);

        JSONObject response;
        String trimmed = (xml != null) ? xml.trim() : "";
        if (trimmed.startsWith("{")) {
            // --- 處理籃球新版 JSON ---
            JSONObject raw = new JSONObject(trimmed);
            JSONObject sr = new JSONObject();
            org.json.JSONArray items = new org.json.JSONArray();
            if (raw.has("response")) {
                JSONObject res = raw.getJSONObject("response");
                // 自動識別 List 接口 (根) 或 More 接口 (GAMES 下)
//                JSONObject dataPool = res.has("GAMES") ? res.getJSONObject("GAMES") : res;
//                for (String key : dataPool.keySet()) {
//                    if (key.toUpperCase().startsWith("GAME")) {
//                        JSONObject gData = dataPool.getJSONObject(key);
//                        if (gData != null) {
//                            // 封裝成統一結構
//                            items.put(new JSONObject().put("game", gData).put("hasEC", "N"));
//                        }
//                    }
//                }
                JSONObject dataPool = res.has("GAMES") ? res.getJSONObject("GAMES") : res;

                Iterator<String> keys = dataPool.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (key.toUpperCase().startsWith("GAME")) {
                        JSONObject gData = dataPool.optJSONObject(key);
                        if (gData != null) {
                            items.put(
                                    new JSONObject().put("game", gData).put("hasEC", "N")
                            );
                        }
                    }
                }
                sr.put("ec", items);
                sr.put("game", items);
                sr.put("totalDataCount", items.length());
                sr.put("code", "success");
                response = new JSONObject().put("serverresponse", sr);
                System.out.println("Response:" + response.toString());

            }
        }
    }

    @Test
    public void BaseHandleTest2() throws IOException, JSONException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/java/com/hrm/application/test/sport/BK_R.json"));
        String xml = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(xml);

        JSONObject response;
    }
    @Test
    public void BaseHandleXmlTest() throws IOException, JSONException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/java/com/hrm/application/test/sport/BK_R.json"));
        String xml = new String(bytes, StandardCharsets.UTF_8);
//        System.out.println(xml);
//        JsonNode response = XMLUtils.xml2json(xml);
        //        for (String key : response.keySet()) {
//            if (key.startsWith("GAME_")) {
//                JSONObject gameObj = response.getJSONObject(key);
//                ecArray.put(gameObj);
//            }
//        }

// 新的 jsonObject
        JSONObject raw = new JSONObject(xml);
        JSONObject response;

        JSONArray ecArray = new JSONArray();
        JSONArray gameArray = new JSONArray();
        boolean hasGames = false;

        if (raw.has("response")) {
            JSONObject res = raw.getJSONObject("response");
//            System.out.println("res: " + res.toString());

            hasGames = res.has("GAMES");
            JSONObject dataPool;


            Iterator<String> keys = res.keys();
            if (hasGames) {
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (key.startsWith("GAME_")) {
                        dataPool = res.getJSONObject("GAMES");
                        hasGames = true;
                        gameArray.put(dataPool.getJSONObject(key));
                    }
                }
            } else {
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (key.startsWith("GAME_")) {
                        dataPool = res;
                        ecArray.put(dataPool.getJSONObject(key));
                    }
                }
            }

            System.out.println("hasGames: " + hasGames);

            // === 組最終回傳物件 ===
            JSONObject newObj = new JSONObject();
            if (hasGames) {
                newObj.put("game", gameArray);









                newObj.put("totalDataCount", gameArray.length());
            } else {
                newObj.put("ec", ecArray);
                newObj.put("totalDataCount", ecArray.length());
            }
            JSONObject sr = new JSONObject();
            sr.put("ec", newObj);
            sr.put("game", items);
            sr.put("totalDataCount", items.length());
            sr.put("code", "success");
            response = new JSONObject().put("serverresponse", sr);
            System.out.println("Response:" + response.toString());
        }
    }


    @Test
    public void parseServerresponseTest() {

    }
}
