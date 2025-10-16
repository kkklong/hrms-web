package com.hrm.application.model.bo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LeaveRecordDateTimeBO implements Serializable {

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
     * 班次類型，如早班、午班、大夜班
     */
    private String shiftType;

    /**
     * 包含休息1小時(不包含:0、包含:1) ，如是1的話，請假時數要扣1小時
     */
    private Boolean includesBreak;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getCountVal() {
        return countVal;
    }

    public void setCountVal(Double countVal) {
        this.countVal = countVal;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public Boolean getIncludesBreak() {
        return includesBreak;
    }

    public void setIncludesBreak(Boolean includesBreak) {
        this.includesBreak = includesBreak;
    }
}
