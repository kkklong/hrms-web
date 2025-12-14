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
        setFlex(left);
        setFlex(center);
        setFlex(right);

        getContent().add(left, center, right);
        getContent().setWidthFull();
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        setFlex(getContent());
    }

    private void setFlex(FlexLayout layout) {
        layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        layout.getStyle().set("gap", "0 0.75rem");
    }

    public void addLeft(Component... components) {
        Objects.requireNonNull(components, "Components should not be null");
        left.add(Arrays.asList(components));
    }

    public void addCenter(Component... components) {
        Objects.requireNonNull(components, "Components should not be null");
        center.add(Arrays.asList(components));
    }

    public void addRight(Component... components) {
        Objects.requireNonNull(components, "Components should not be null");
        right.add(Arrays.asList(components));
    }


}
