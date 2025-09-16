package com.hrm.application.test.sport;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "serverresponse")
public class ServerResponse {

    private int status;
    private String msg;
    private String code_message;
    private String username;
    private String mid;
    private String uid;
    private String passwd_safe;
    private int ltype;
    private String currency;
    private String odd_f;
    private int pay_type;
    private String blackBoxStatus;
    private String domain;
    private String t_link;

    // Getters and Setters ...

    @JacksonXmlProperty(localName = "status")
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    @JacksonXmlProperty(localName = "msg")
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    @JacksonXmlProperty(localName = "code_message")
    public String getCode_message() { return code_message; }
    public void setCode_message(String code_message) { this.code_message = code_message; }

    @JacksonXmlProperty(localName = "username")
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @JacksonXmlProperty(localName = "mid")
    public String getMid() { return mid; }
    public void setMid(String mid) { this.mid = mid; }

    @JacksonXmlProperty(localName = "uid")
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    @JacksonXmlProperty(localName = "passwd_safe")
    public String getPasswd_safe() { return passwd_safe; }
    public void setPasswd_safe(String passwd_safe) { this.passwd_safe = passwd_safe; }

    @JacksonXmlProperty(localName = "ltype")
    public int getLtype() { return ltype; }
    public void setLtype(int ltype) { this.ltype = ltype; }

    @JacksonXmlProperty(localName = "currency")
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @JacksonXmlProperty(localName = "odd_f")
    public String getOdd_f() { return odd_f; }
    public void setOdd_f(String odd_f) { this.odd_f = odd_f; }

    @JacksonXmlProperty(localName = "pay_type")
    public int getPay_type() { return pay_type; }
    public void setPay_type(int pay_type) { this.pay_type = pay_type; }

    @JacksonXmlProperty(localName = "blackBoxStatus")
    public String getBlackBoxStatus() { return blackBoxStatus; }
    public void setBlackBoxStatus(String blackBoxStatus) { this.blackBoxStatus = blackBoxStatus; }

    @JacksonXmlProperty(localName = "domain")
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    @JacksonXmlProperty(localName = "t_link")
    public String getT_link() { return t_link; }
    public void setT_link(String t_link) { this.t_link = t_link; }
}