package com.hrm.application.util;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class NotificationUtil {

    private static final int DEFAULT_DURATION = 3000;

    public static void success(String message) {
        show(message, Notification.Position.MIDDLE, NotificationVariant.LUMO_SUCCESS, DEFAULT_DURATION);
    }

    public static void error(String message) {
        show(message, Notification.Position.MIDDLE, NotificationVariant.LUMO_ERROR, DEFAULT_DURATION);
    }

    public static void info(String message) {
        show(message, Notification.Position.MIDDLE, NotificationVariant.LUMO_PRIMARY, DEFAULT_DURATION);
    }

    public static void warn(String message) {
        show(message, Notification.Position.MIDDLE, NotificationVariant.LUMO_CONTRAST, DEFAULT_DURATION);
    }

    public static void custom(String message, Notification.Position position, NotificationVariant variant, int duration) {
        show(message, position, variant, duration);
    }

    private static void show(String message, Notification.Position position, NotificationVariant variant, int duration) {
        Span content = new Span(message);
        Notification notification = new Notification(content);
        notification.setPosition(position);
        notification.setDuration(duration);
        notification.addThemeVariants(variant);
        notification.open();
    }

    public static void persistent(String message, NotificationVariant variant) {
        Notification notification = new Notification();
        notification.setPosition(Notification.Position.BOTTOM_START);
        notification.setDuration(0); // 永不自動關閉

        Span content = new Span(message);
        Button closeButton = new Button("關閉", e -> notification.close());

        HorizontalLayout layout = new HorizontalLayout(content, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
