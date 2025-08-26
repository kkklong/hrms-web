package com.hrm.application.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ApprovalScopeType {
    GLOBAL("GLOBAL", "全局"),
    COMPANY("COMPANY", "公司"),
    DEPARTMENT("DEPARTMENT", "部門"),
    EMPLOYEE("EMPLOYEE", "員工");

    private final String value;  // 後端傳遞/儲存用
    private final String label;  // UI 顯示用

    ApprovalScopeType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }


    private static final Map<String, ApprovalScopeType> BY_VALUE =
            Arrays.stream(values()).collect(Collectors.toMap(
                    e -> e.value.toUpperCase(), Function.identity()));


    public static ApprovalScopeType fromValue(String raw) {
        if (raw == null) return null;
        return BY_VALUE.get(raw.trim().toUpperCase());
    }
}