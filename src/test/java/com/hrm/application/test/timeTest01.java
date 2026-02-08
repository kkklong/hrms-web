package com.hrm.application.test;

import com.hrm.application.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class timeTest01 {

    @Test
    public void timeTest01() {
        String time1 = LocalDateTime.now() + "";
        String time3 = System.currentTimeMillis()  + "";

//        System.out.println(new Date(1762372887000l));
        System.out.println(time3);

    }


}
