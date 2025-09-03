package com.hrm.application.model;

import java.util.function.Supplier;

public class Validator {
    private final String errorMessage;
    private final Supplier<Boolean> invalidCondition;

    public Validator(String errorMessage, Supplier<Boolean> invalidCondition) {
        this.errorMessage = errorMessage;
        this.invalidCondition = invalidCondition;
    }

    public boolean isInvalid() {
        return invalidCondition.get();
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
