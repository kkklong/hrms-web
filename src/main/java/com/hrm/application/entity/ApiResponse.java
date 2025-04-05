package com.hrm.application.entity;

import java.util.Arrays;

public class ApiResponse<T> {

    Integer code;
    String message;
    T data;
    String[] errorFields;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String[] getErrorFields() {
        return errorFields;
    }

    public void setErrorFields(String[] errorFields) {
        this.errorFields = errorFields;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", errorFields=" + Arrays.toString(errorFields) +
                '}';
    }
}
