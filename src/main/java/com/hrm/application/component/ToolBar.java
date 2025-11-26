package com.hrm.application.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import java.util.Arrays;
import java.util.Objects;

public class ToolBar extends Composite<FlexLayout> {

    private final FlexLayout left = new FlexLayout();
    private final FlexLayout center = new FlexLayout();
    private final FlexLayout right = new FlexLayout();

    public ToolBar() {
        left.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        left.getStyle().set("gap", "0 0.75rem");
        left.setVisible(false);
        center.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        center.getStyle().set("gap", "0 0.75rem");
        center.setVisible(false);
        right.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        right.getStyle().set("gap", "0 0.75rem");
        right.setVisible(false);

        getContent().add(left, center, right);
        getContent().setWidthFull();
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setFlexWrap(FlexLayout.FlexWrap.WRAP);
        getContent().getStyle().set("gap", "0 0.75rem");
    }

    public void addLeft(Component... components) {
        Objects.requireNonNull(components, "Components should not be null");
        left.add(Arrays.asList(components));
        left.setVisible(true);
    }

    public void addCenter(Component... components) {
        Objects.requireNonNull(components, "Components should not be null");
        center.add(Arrays.asList(components));
        center.setVisible(true);
    }

    public void addRight(Component... components) {
        Objects.requireNonNull(components, "Components should not be null");
        right.add(Arrays.asList(components));
        right.setVisible(true);
    }


}
