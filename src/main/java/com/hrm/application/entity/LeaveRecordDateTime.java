package com.hrm.application.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LeaveRecordDateTime implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 請假開始時間
     */
    private LocalDateTime startDate;

    /**
     * 請假結束時間
     */
    private LocalDateTime endDate;

    /**
     * 小時計算，可以有小數點
     */
    private Double countVal;

    /**
     * 此申請對應哪個假單
     */
    private Integer leaveRecordsId;

    /**
     * 班次類型，如早班、午班、大夜班
     */
    private String shiftType;

    /**
     * 包含休息1小時(不包含:0、包含:1) ，如是1的話，請假時數要扣1小時
     */
    private Boolean includesBreak;
}
