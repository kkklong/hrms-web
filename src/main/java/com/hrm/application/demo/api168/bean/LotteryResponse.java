package com.hrm.application.demo.api168.bean;

import lombok.Data;

@Data
public class LotteryResponse {
    private int errorCode;
    private String message;
    private Result result;
}
