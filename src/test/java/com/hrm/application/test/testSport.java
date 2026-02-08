package com.hrm.application.test;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.hrm.application.util.XMLUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class testSport {
    static String[] oddsAttrNamesH = {"ior_RH", "ior_OUH", "ior_HOUH", "ior_HRH", "ior_EOE"};
    static String[] oddsAttrNamesC = {"ior_RC", "ior_OUC", "ior_HOUC", "ior_HRC", "ior_EOO"};


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
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/java/com/hrm/application/test/sport/FT_R_gameMore.xml"));
        String xml = new String(bytes, StandardCharsets.UTF_8);
//        System.out.println(xml);

        JsonNode response = XMLUtils.xml2json(xml);
        System.out.println(response);
    }

    @Test
    public void BaseHandleXmlTest() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/java/com/hrm/application/test/sport/BK_RB_more.json"));
        String xml = new String(bytes, StandardCharsets.UTF_8);
//        System.out.println(xml);

        JSONObject response = handleJsonData(xml);
//        System.out.println("response" + response.toString());
        JSONObject serverResonse = response.getJSONObject("serverresponse");
//        System.out.println("serverResonse:{} " + serverResonse.toString());
        JSONArray game = serverResonse.getJSONArray("game");

        List<String> list = new ArrayList<>();
        list.add(game.toString());
//        System.out.println("gamelist" + list.toString());

        List<String> setOddList = setOdds(list);
        System.out.println("setOddList" + setOddList.toString());




//        System.out.println("resultList" + resultList.get(0));


    }

    static JSONObject handleJsonData(String trimmed) throws Exception {
        // --- 處理籃球新版 JSON ---
        JSONObject raw = new JSONObject(trimmed);
        JSONObject sr = new JSONObject();
        JSONObject res = raw.optJSONObject("response");
        JSONArray ecArray = new JSONArray();
        JSONArray gameArray = new JSONArray();
        boolean hasGames = false; // 以Games區分more的資料還是gameList的資料, 分開處理
        if (res != null) {
            hasGames = res.has("GAMES");
            if (hasGames) {
                JSONObject games = res.getJSONObject("GAMES");
                Iterator<String> keys = games.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (key.startsWith("GAME")) {
                        JSONObject gameObj = games.getJSONObject(key);
                        standardizeBasketball(gameObj);
                        gameObj.put("hasEC", "N");
                        gameArray.put(gameObj);
                    }
                }
            } else {
                Iterator<String> keys = res.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (key.startsWith("GAME")) {
                        JSONObject gameObj = res.getJSONObject(key);
                        JSONObject gamesObj = new JSONObject();
                        standardizeBasketball(gameObj);
                        gamesObj.put("hasEC", "N");
                        gamesObj.put("game", gameObj);
                        ecArray.put(gamesObj);
                    }
                }
            }
            // === 組最終回傳物件 ===
            if (hasGames) {
                sr.put("game", gameArray);
                sr.put("totalDataCount", gameArray.length());
            } else {
                sr.put("ec", ecArray);
                sr.put("totalDataCount", ecArray.length());
            }
            sr.put("code", "success");
        }

        // 如果已經包含有效的 serverresponse，則直接使用原始資料
        if (raw.has("serverresponse") && raw.optJSONObject("serverresponse") != null) {
            return raw;
        } else {
            sr.put("code", "success");
            return new JSONObject().put("serverresponse", sr);
        }
    }

    static void standardizeBasketball(JSONObject g) throws JSONException {
        if (g == null) {
            return;
        }
        if (g.has("IS_MASTER")) {
            Object gval = g.get("IS_MASTER");
            g.put("isMaster", gval);
            g.remove("IS_MASTER");
        }

        if (g.has("PLAYS")) {
            JSONObject plays = g.getJSONObject("PLAYS");

            Iterator<String> pKeys = plays.keys();
            while (pKeys.hasNext()) {
                String pKey = pKeys.next();
                Object marketObj = plays.get(pKey);

                if (!(marketObj instanceof JSONObject)) {
                    continue;
                }

                JSONObject market = (JSONObject) marketObj;
                Iterator<String> mKeys = market.keys();

                while (mKeys.hasNext()) {
                    String mk = mKeys.next();
                    Object val = market.get(mk);
                    String normalizedKey = mk;
                    if (mk.startsWith("MS_")) {
                        normalizedKey = mk.substring(3); // 去掉 "MS_"
                    }

                    String upperKey = normalizedKey.toUpperCase();

                    // === A. 让球 ===
                    if (upperKey.equals("IOR_RH") || upperKey.equals("IOR_REH")) g.put("ior_RH", val);
                    else if (upperKey.equals("IOR_RC") || upperKey.equals("IOR_REC")) g.put("ior_RC", val);
                    else if (upperKey.equals("RATIO_R") || upperKey.equals("RATIO_RE")) g.put("ratio", val);

                        // B. 全场大小 (Total)
                    else if (upperKey.equals("IOR_OUH") || upperKey.equals("IOR_ROUH")) g.put("ior_OUH", val);
                    else if (upperKey.equals("IOR_OUC") || upperKey.equals("IOR_ROUC")) g.put("ior_OUC", val);
                    else if (upperKey.equals("RATIO_O") || upperKey.equals("RATIO_ROUO") || upperKey.equals("RATIO_OUO")) g.put("ratio_o", val);
                    else if (upperKey.equals("RATIO_U") || upperKey.equals("RATIO_ROUU") || upperKey.equals("RATIO_OUU")) g.put("ratio_u", val);

                        // C. 独赢 (Moneyline)
                    else if (upperKey.equals("IOR_MH") || upperKey.equals("IOR_RMH")) g.put("ior_MH", val);
                    else if (upperKey.equals("IOR_MC") || upperKey.equals("IOR_RMC")) g.put("ior_MC", val);
                    else if (upperKey.equals("IOR_MN") || upperKey.equals("IOR_RMN")) g.put("ior_MN", val);

                        // D. 单双 (Odd/Even)
                    else if (upperKey.equals("IOR_EOO") || upperKey.equals("IOR_REOO")) g.put("ior_EOO", val);
                    else if (upperKey.equals("IOR_EOE") || upperKey.equals("IOR_REOE")) g.put("ior_EOE", val);

                        // E. 全场球队得分大小 (Team Totals) - 主隊
                    else if (upperKey.equals("IOR_OUHO") || upperKey.equals("IOR_ROUHO")) g.put("ior_OUHO", val);
                    else if (upperKey.equals("IOR_OUHU") || upperKey.equals("IOR_ROUHU")) g.put("ior_OUHU", val);
                    else if (upperKey.equals("RATIO_OH") || upperKey.equals("RATIO_ROUHO")) g.put("ratio_ouho", val);
                    else if (upperKey.equals("RATIO_UH") || upperKey.equals("RATIO_ROUHU")) g.put("ratio_ouhu", val);

                        // F. 全场球队得分大小 (Team Totals) - 客隊
                    else if (upperKey.equals("IOR_OUCO") || upperKey.equals("IOR_ROUCO")) g.put("ior_OUCO", val);
                    else if (upperKey.equals("IOR_OUCU") || upperKey.equals("IOR_ROUCU")) g.put("ior_OUCU", val);
                    else if (upperKey.equals("RATIO_OC") || upperKey.equals("RATIO_ROUCO")) g.put("ratio_ouco", val);
                    else if (upperKey.equals("RATIO_UC") || upperKey.equals("RATIO_ROUCU")) g.put("ratio_oucu", val);

                        // G. 半场让球 (Half Time Handicap)
                    else if (upperKey.equals("HALF_IOR_RH")) g.put("ior_HRH", val);
                    else if (upperKey.equals("HALF_IOR_RC")) g.put("ior_HRC", val);
                    else if (upperKey.equals("HALF_RATIO_R")) g.put("hratio", val);

                        // H. 半场大小 (Half Time Over/Under)
                    else if (upperKey.equals("HALF_IOR_OUH")) g.put("ior_HOUH", val);
                    else if (upperKey.equals("HALF_IOR_OUC")) g.put("ior_HOUC", val);
                    else if (upperKey.equals("HALF_RATIO_OUO")) g.put("hratio_o", val);
                    else if (upperKey.equals("HALF_RATIO_OUU")) g.put("hratio_u", val);

                    else if (upperKey.equals("HALF_GID")) g.put("HGID", val);

//                    else if (upperKey.equals("SW_OU") || upperKey.equals("SW_ROU")) g.put("sw_OU", val);
//                    else if (upperKey.equals("SW_R") || upperKey.equals("SW_RE")) g.put("sw_R", val);
//                    else if (upperKey.equals("SW_EO") || upperKey.equals("SW_REO")) g.put("sw_EO", val);
                }
            }
            g.remove("PLAYS");
        }
        // 2. 平鋪 SCORE (比分)
        if (g.has("SCORE")) {
            JSONObject s = g.getJSONObject("SCORE");

            Iterator<String> sKeys = s.keys();
            List<String> score = new ArrayList<>();

            // 先 snapshot，避免 iteration 中修改結構
            while (sKeys.hasNext()) {
                score.add(sKeys.next());
            }

            for (String sk : score) {
                Object val = s.get(sk);

                // 全小寫
//                g.put(sk.toLowerCase(), val);

                if (sk.startsWith("SC_")) {
                    g.put(sk.replace("SC_", "sc_"), val);
                }
            }
        }

        //部分作兼容
        String[] COPY_KEYS = {"GID", "TEAM_C", "TEAM_H", "LEAGUE", "STRONG", "SHOWTYPE", "RUNNING", "MORE", "GIDM", "GNUM_C", "GNUM_H", "RETIME", "TIMER"};
        for (String key : COPY_KEYS) {
            if (g.has(key) && !g.has(key.toLowerCase())) {
                g.put(key.toLowerCase(), g.get(key));
            }
        }
        //移除多餘的key
        String[] REMOVE_KEYS = {"TEAM_C", "TEAM_H", "LEAGUE", "STRONG", "SHOWTYPE", "RUNNING", "GIDM", "GNUM_C", "GNUM_H", "RETIME", "SCORE"};
        for (String key : REMOVE_KEYS) {
            if (g.has(key)) {
                g.remove(key);
            }
        }

        // 4. Session 映射與名稱自動更名 (簡體中文)
        String cn = "";
        if (g.has("MS")) {
            String se = extractMs(g);
            cn = se.replace("3", "第一节").replace("4", "第二节").replace("5", "第三节").replace("6", "第四节")
                    .replace("1", "上半场").replace("2", "下半场").replace("", ""); // MS""為全场賽事
            g.put("session", cn);
        } else {
            g.put("session", "");
        }
        if (g.has("team_h")) {
            String tH = g.getString("team_h");
            String tC = g.getString("team_c");
            if (tH != null && !tH.contains(cn) && cn != "") {
                g.put("team_h", tH + " - (" + cn + ")");
                g.put("team_c", tC + " - (" + cn + ")");
                g.put("TEAM_H", g.get("team_h")); g.put("TEAM_C", g.get("team_c"));
            }
        }
    }

    static String extractMs(JSONObject g) throws JSONException {
        return Optional.ofNullable(g.get("MS"))
                .map(Object::toString)
                .filter(s -> s.contains("_"))
                .map(s -> s.split("_", 2)[1])
                .orElse("");
    }

    public static List<String> setOdds(List<String> datas) throws JSONException {
        List<String> lists = new ArrayList<>();

        for (String item : datas) {
            if (item == null) {
                continue;
            }

            JSONArray array = new JSONArray(item);

            for (int i = 0; i < array.length(); i++) {
                JSONObject resultJson = array.getJSONObject(i);
                for (int h = 0; h < oddsAttrNamesH.length; h++) {
                    String key = oddsAttrNamesH[h];
                    handleOddsKey(resultJson, key);
                }
                for (int c = 0; c < oddsAttrNamesC.length; c++) {
                    String key = oddsAttrNamesC[c];
                    handleOddsKey(resultJson, key);
                }
                JSONArray resultArray = new JSONArray();
                resultArray.put(resultJson);
                lists.add(resultArray.toString());
            }
        }
        return lists;
    }

    private static void handleOddsKey(JSONObject json, String key) throws JSONException {
        double val;
        if (!json.has(key)) {
            return;
        }
        Object v = json.get(key);
        System.out.print(" Current handle Key: " + key);

        if (v instanceof Number) {
            val = ((Number) v).doubleValue();
        } else if (v instanceof String) {
            System.out.print("; Current handle: " + v);
            val = Double.parseDouble(((String) v).trim());
            System.out.println("; Finish handle: " + val);
        } else {
            return; // 不是可處理的型別
        }

        if (val == 0) {
            return;
        }
        json.put(key, String.format("%.2f", val + 0.04d));
    }

    @Test
    public void parseServerresponseTest() {

    }
}
