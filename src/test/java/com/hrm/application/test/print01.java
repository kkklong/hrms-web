package com.hrm.application.test;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class print01 {
    @Test
    public void test01(){
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
        String ip = Jsoup.connect("https://api.ipify.org").ignoreContentType(true).execute().body();
        System.out.println(ip);
    }


}
