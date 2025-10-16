package com.hrm.application.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ShiftSchedules {

    /**
     * 排班ID(自動產生)
     */
    private Integer id;

    /**
     * 員工ID
     */
    private Integer employeeId;

    /**
     * 員工別名
     */
    private String nickName;

    /**
     * 部門ID
     */
    private Integer departmentId;

    /**
     * 部門名稱
     */
    private String departmentName;

    /**
     * 班別，如：日班、夜班等
     */
    private String shiftTypes;

    /**
     * 排班的具體日期
     */
    private LocalDate shiftDate;

    /**
     * 排班狀態，0：上班，1：請假
     */
    private Byte status;

    /**
     * 周別型別，1 表示第1周，2 表示第2周
     */
    private Byte weekType;

    /**
     * 備註，記錄額外資訊，如特殊說明等
     */
    private String remark;

    /**
     * 班別修改狀態 0: 可修改、1: 不可修改
     */
    private Byte actionType;

    /**
     * 班別色碼
     */
    private String shiftColorCode;

    /**
     * 員工編號
     */
    private String employeeNumber;

    public ShiftSchedules() {
    }

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

    public String getShiftTypes() {
        return shiftTypes;
    }

    public void setShiftTypes(String shiftTypes) {
        this.shiftTypes = shiftTypes;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getWeekType() {
        return weekType;
    }

    public void setWeekType(Byte weekType) {
        this.weekType = weekType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Byte getActionType() {
        return actionType;
    }

    public void setActionType(Byte actionType) {
        this.actionType = actionType;
    }

    public String getShiftColorCode() {
        return shiftColorCode;
    }

    public void setShiftColorCode(String shiftColorCode) {
        this.shiftColorCode = shiftColorCode;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    @Override
    public String toString() {
        return "ShiftSchedules{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", nickName='" + nickName + '\'' +
                ", departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", shiftTypes='" + shiftTypes + '\'' +
                ", shiftDate=" + shiftDate +
                ", status=" + status +
                ", weekType=" + weekType +
                ", remark='" + remark + '\'' +
                ", actionType=" + actionType +
                ", shiftColorCode='" + shiftColorCode + '\'' +
                '}';
    }
}