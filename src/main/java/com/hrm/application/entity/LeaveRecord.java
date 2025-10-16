package com.hrm.application.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LeaveRecord {

    /**
     * ID(自動產生)
     */
    private Integer id;

    /**
     * 英文名
     */
    private String nickName;

    /**
     * 部⻔ID
     */
    private String departmentId;

    /**
     * 如:事假、病假....
     */
    private String leaveTypes;

    /**
     * 申請時間
     */
    private LocalDateTime createdDate;

    /**
     * 時間
     */
    private Double countVal;

    /**
     * 請假原因
     */
    private String reason;

    /**
     * 備註
     */
    private String remark;

    /**
     * Pending：申請已經提交；Approved：申請已經獲得批準；Rejected：申請被拒絕；Cancelled：申請被員工或管理員取消
     */
    private Byte status;

    /**
     * 請假流程狀態（1: 組長審核中、2:技術長審核中、3:總經理審核中、4:人資審核中)
     */
    private Byte approvalStage;

    /**
     * 記錄每階段審核人與時間
     */
    private String historyReview;

    /**
     * 0:false;1:true (是否需要附件)
     */
    private Boolean attachmentRequired;

    /**
     * 是否能被當前登入者審核
     */
    private boolean eligibleForApproval;

    /**
     * 請假具體日期及班次資訊
     */
    private List<LeaveRecordDateTime> leaveDates;

    /**
     * 相關檔案列表
     */
    private List<FileData> files;

    /**
     * (前端計算)請假開始時間
     */
    private LocalDateTime startDate;

    /**
     * (前端計算)請假結束時間
     */
    private LocalDateTime endDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getLeaveTypes() {
        return leaveTypes;
    }

    public void setLeaveTypes(String leaveTypes) {
        this.leaveTypes = leaveTypes;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Double getCountVal() {
        return countVal;
    }

    public void setCountVal(Double countVal) {
        this.countVal = countVal;
    }

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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getApprovalStage() {
        return approvalStage;
    }

    public void setApprovalStage(Byte approvalStage) {
        this.approvalStage = approvalStage;
    }

    public String getHistoryReview() {
        return historyReview;
    }

    public void setHistoryReview(String historyReview) {
        this.historyReview = historyReview;
    }

    public Boolean getAttachmentRequired() {
        return attachmentRequired;
    }

    public void setAttachmentRequired(Boolean attachmentRequired) {
        this.attachmentRequired = attachmentRequired;
    }

    public boolean isEligibleForApproval() {
        return eligibleForApproval;
    }

    public void setEligibleForApproval(boolean eligibleForApproval) {
        this.eligibleForApproval = eligibleForApproval;
    }

    public List<LeaveRecordDateTime> getLeaveDates() {
        return leaveDates;
    }

    public void setLeaveDates(List<LeaveRecordDateTime> leaveDates) {
        this.leaveDates = leaveDates;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void setFiles(List<FileData> files) {
        this.files = files;
    }

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
}
