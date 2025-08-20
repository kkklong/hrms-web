package com.hrm.application.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Option<T> {

    @JsonAlias({"name","key"})
    private String name;
    private T value;
    private T referenceValue;

    public Option(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getReferenceValue() {
        return referenceValue;
    }

    public void setReferenceValue(T referenceValue) {
        this.referenceValue = referenceValue;
    }

}
