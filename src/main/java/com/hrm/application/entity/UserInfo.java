package com.hrm.application.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserInfo implements Serializable {

    /**
     * 職員ID(自動產生)
     */
    private Integer id;

    /**
     * 帳號
     */
    private String account;

    /**
     * 全名
     */
    private String fullName;

    /**
     * 英文名
     */
    private String nickName;

    /**
     * 職位
     */
    private String position;

    /**
     * 年資
     */
    private Double seniority;

    /**
     * 特休 以⼩時為單位
     */
    private Double paidVacation;

    /**
     * 特休 以⼩時為單位
     */
    private Double lastYearPaidVacation;

    /**
     * 性別
     */
    private String gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 生日
     */
    private String phone;

    /**
     * 信箱
     */
    private String email;

    /**
     * skype帳號
     */
    private String skype;

    /**
     * TELEGRAM帳號
     */
    private String telegram;

    /**
     * ⼊職時間
     */
    private LocalDate entryDate;

    /**
     * ⺟部⻔
     */
    private Integer departmentParent;

    /**
     * 部門ID
     */
    private Integer departmentId;

    /**
     * 部⻔名稱
     */
    private String departmentName;

    /**
     * 描述
     */
    private String description;

    /**
     * 主管英文名
     */
    private String managerNickName;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 加班是否可換錢
     */
    private Integer overtimeType;

    private String emergencyContact;
    private String address;
    private String relationship;
    private String emergencyContactPhone;
    private String idNumber;
    private String employeeNumber;
    private String highestEducationLevel;
    private String emergencyContactAddress;
    private String registeredAddress;
}
