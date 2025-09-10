package com.hrm.application.demo.api168.bean;

import lombok.Data;

@Data
public class Result {
    private int businessCode;
    private String message;
    private LotteryData data;
}
