package com.hrm.application.entity;

import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(chain = true)
public class LeaveSpecialRecord {

    private Integer id;

    private Integer employeeId;

    private String nickName;

    private Integer departmentId;

    private String departmentName;

    private String leaveTypes;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String chineseName;

    private String salaryStandard;

    private Boolean fullAttendanceBonus;

    private Float minLeaveUnit;

    private Integer maxLeaveDays;

    private Boolean continuousLeave;

    private Boolean advanceApplication;

    private String description;

    private Float settlementCount;

    private Boolean attachmentRequired;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getLeaveTypes() {
        return leaveTypes;
    }

    public void setLeaveTypes(String leaveTypes) {
        this.leaveTypes = leaveTypes;
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

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getSalaryStandard() {
        return salaryStandard;
    }

    public void setSalaryStandard(String salaryStandard) {
        this.salaryStandard = salaryStandard;
    }

    public Boolean getFullAttendanceBonus() {
        return fullAttendanceBonus;
    }

    public void setFullAttendanceBonus(Boolean fullAttendanceBonus) {
        this.fullAttendanceBonus = fullAttendanceBonus;
    }

    public Float getMinLeaveUnit() {
        return minLeaveUnit;
    }

    public void setMinLeaveUnit(Float minLeaveUnit) {
        this.minLeaveUnit = minLeaveUnit;
    }

    public Integer getMaxLeaveDays() {
        return maxLeaveDays;
    }

    public void setMaxLeaveDays(Integer maxLeaveDays) {
        this.maxLeaveDays = maxLeaveDays;
    }

    public Boolean getContinuousLeave() {
        return continuousLeave;
    }

    public void setContinuousLeave(Boolean continuousLeave) {
        this.continuousLeave = continuousLeave;
    }

    public Boolean getAdvanceApplication() {
        return advanceApplication;
    }

    public void setAdvanceApplication(Boolean advanceApplication) {
        this.advanceApplication = advanceApplication;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getSettlementCount() {
        return settlementCount;
    }

    public void setSettlementCount(Float settlementCount) {
        this.settlementCount = settlementCount;
    }

    public Boolean getAttachmentRequired() {
        return attachmentRequired;
    }

    public void setAttachmentRequired(Boolean attachmentRequired) {
        this.attachmentRequired = attachmentRequired;
    }

    @Override
    public String toString() {
        return "LeaveSpecialRecord{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", nickName='" + nickName + '\'' +
                ", departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", leaveTypes='" + leaveTypes + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", salaryStandard='" + salaryStandard + '\'' +
                ", fullAttendanceBonus=" + fullAttendanceBonus +
                ", minLeaveUnit=" + minLeaveUnit +
                ", maxLeaveDays=" + maxLeaveDays +
                ", continuousLeave=" + continuousLeave +
                ", advanceApplication=" + advanceApplication +
                ", description='" + description + '\'' +
                ", settlementCount=" + settlementCount +
                ", attachmentRequired=" + attachmentRequired +
                '}';
    }
}

