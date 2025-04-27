package com.hrm.application.menu;

import com.vaadin.flow.component.icon.VaadinIcon;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MenuRouter {
    String label();
    VaadinIcon icon();
}
