package com.hrm.application.views.employee;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

public class DepartmentChangeDialog extends Dialog {

    public interface DepartmentChangeListener {
        void onDefaultScheduleSelected();
        void onKeepCurrentScheduleSelected();
    }

    private DepartmentChangeListener listener;

    public DepartmentChangeDialog(DepartmentChangeListener listener) {
        this.listener = listener;

        setCloseOnOutsideClick(false);
        setCloseOnEsc(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new H3("班表處理選擇"));
        dialogLayout.add(new Span("員工部門已變更，請選擇班表處理方式："));

        RadioButtonGroup<String> scheduleOptions = new RadioButtonGroup<>();
        scheduleOptions.setItems("新部門預設班表", "延用班表");
        scheduleOptions.setValue("延用班表");

        Button confirmButton = new Button("確認", event -> {
            if (listener != null) {
                String selectedOption = scheduleOptions.getValue();
                if ("新部門預設班表".equals(selectedOption)) {
                    listener.onDefaultScheduleSelected();
                } else if ("延用班表".equals(selectedOption)) {
                    listener.onKeepCurrentScheduleSelected();
                }
            }
            close();
        });

        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("取消", event -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonLayout.setSpacing(true);

        dialogLayout.add(scheduleOptions, buttonLayout);

        add(dialogLayout);
    }
}
