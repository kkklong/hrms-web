package com.hrm.application.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Employee {
    /**
     * 職員ID(自動產生)
     */
    private Integer id;

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
     * 在職 0:離職,1:在職,2:留職,3:其他
     */
    private Byte status;

    /**
     * 薪資
     */
    private Long salary;

    /**
     * 帳號
     */
    private String account;

    /**
     * 密碼
     */
    private String password;

    /**
     * 部⻔ID
     */
    private Integer departmentId;

    /**
     * 性別
     */
    private String gender;

    /**
     * 角色ID
     */
    private Integer roleId;
    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 聯絡電話
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
     * 離職時間
     */
    private LocalDate outDate;

    /**
     * 更新密碼時間
     */
    private LocalDateTime passwordUpdateTime;

    /**
     * 緊急聯絡人
     */
    private String emergencyContact;

    /**
     * 通訊地址
     */
    private String address;

    /**
     * 備註說明
     */
    private String remark;

    /**
     * 所在樓層
     */
    private String floor;

    /**
     * 座位編號
     */
    private String seatNumber;

    /**
     * 公司
     */
    private Integer company;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }


    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public LocalDate getOutDate() {
        return outDate;
    }

    public void setOutDate(LocalDate outDate) {
        this.outDate = outDate;
    }

    public LocalDateTime getPasswordUpdateTime() {
        return passwordUpdateTime;
    }

    public void setPasswordUpdateTime(LocalDateTime passwordUpdateTime) {
        this.passwordUpdateTime = passwordUpdateTime;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Integer getCompany() {
        return company;
    }

    public void setCompany(Integer company) {
        this.company = company;
    }

    Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * 緊急聯絡人與員工的關係，例如父母、配偶、朋友等
     */
    private String relationship;

    /**
     * 緊急聯絡人電話
     */
    private String emergencyContactPhone;

    /**
     * 加班是否換錢，預設是0，目前只有eg的java是1
     */
    private Integer overtimeType;

    /**
     * 勞保費用
     */
    private BigDecimal laborInsuranceFee;

    /**
     * 健保費用
     */
    private BigDecimal healthInsuranceFee;

    /**
     * 假日津貼
     */
    private BigDecimal holidayDutyAllowance;

    /**
     * 午班津貼
     */
    private BigDecimal afternoonShiftAllowance;

    /**
     * 晚班津貼
     */
    private BigDecimal nightShiftAllowance;

    /**
     * 全勤津貼
     */
    private BigDecimal fullAttendanceBonus;

    /**
     * 身份證字號
     */
    private String idNumber;

    /**
     * 伙食津貼
     */
    private Integer mealAllowance;

    /**
     * 員工編號
     */
    private String employeeNumber;

    /**
     * 最高學歷
     */
    private String highestEducationLevel;

    /**
     * 緊急連絡人通訊地址
     */
    private String emergencyContactAddress;

    /**
     * 戶籍地址
     */
    private String registeredAddress;

    /**
     * 戶籍勞退自提0%；1%~6%
     */
    private Float voluntaryPensionContribution;

    /**
     * 投保眷口數
     */
    private Integer insuredDependentsCount;

    /**
     * 代扣稅款
     */
    private Integer withholdingTax;

    /**
     * 公司付擔勞保費用
     */
    private Integer companyLaborInsuranceFee;

    /**
     * 公司付擔健保費用
     */
    private Integer companyHealthInsuranceFee;

    /**
     * 是否將班表更新為新部門的預設班表
     */
    private Boolean updateShiftToDefault;

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", position='" + position + '\'' +
                ", status=" + status +
                ", salary=" + salary +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", departmentId=" + departmentId +
                ", gender='" + gender + '\'' +
                ", roleId=" + roleId +
                ", birthday=" + birthday +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", skype='" + skype + '\'' +
                ", telegram='" + telegram + '\'' +
                ", entryDate=" + entryDate +
                ", outDate=" + outDate +
                ", passwordUpdateTime=" + passwordUpdateTime +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", address='" + address + '\'' +
                ", remark='" + remark + '\'' +
                ", floor='" + floor + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", role=" + role +
                '}';
    }
}
