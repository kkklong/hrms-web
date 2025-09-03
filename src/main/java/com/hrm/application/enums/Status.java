package com.hrm.application.enums;

public enum Status {

    TRUE("是", 1, (byte) 1, true),
    FALSE("否", 0, (byte) 0, false);

    String label;
    Integer intValue;
    Byte byteValue;
    Boolean booleanValue;

    Status(String label, Integer intValue, Byte byteValue, Boolean booleanValue) {
        this.intValue = intValue;
        this.byteValue = byteValue;
        this.label = label;
        this.booleanValue = booleanValue;
    }

    public String getLabel() {
        return label;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public Byte getByteValue() {
        return byteValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public static Status fromValue(Byte value) {
        for (Status status : Status.values()) {
            if (status.getByteValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    public static Status fromBoolean(Boolean booleanValue) {
        for (Status status : Status.values()) {
            if (status.getBooleanValue().equals(booleanValue)) {
                return status;
            }
        }
        return null;
    }
}
