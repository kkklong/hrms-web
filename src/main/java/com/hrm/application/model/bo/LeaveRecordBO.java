package com.hrm.application.model.bo;


import java.io.Serializable;
import java.util.List;

public class LeaveRecordBO implements Serializable {

    private String reason;

    private String remark;

    private Integer leaveSpecialRecordsId;

    private List<LeaveRecordDateTimeBO> leaveDates;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getLeaveSpecialRecordsId() {
        return leaveSpecialRecordsId;
    }

    public void setLeaveSpecialRecordsId(Integer leaveSpecialRecordsId) {
        this.leaveSpecialRecordsId = leaveSpecialRecordsId;
    }

    public List<LeaveRecordDateTimeBO> getLeaveDates() {
        return leaveDates;
    }

    public void setLeaveDates(List<LeaveRecordDateTimeBO> leaveDates) {
        this.leaveDates = leaveDates;
    }
}