package com.hrm.application.entity;

import java.io.Serializable;
import java.util.List;

public class Menu implements Serializable {
    private String code;
    private String text;
    private List<Menu> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Menu> getData() {
        return data;
    }

    public void setData(List<Menu> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "code='" + code + '\'' +
                ", text='" + text + '\'' +
                ", data=" + data +
                '}';
    }
}
