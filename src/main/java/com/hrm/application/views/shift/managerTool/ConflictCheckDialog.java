package com.hrm.application.views.shift.managerTool;

import com.hrm.application.model.Option;
import com.hrm.application.service.ShiftScheduleService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;

import java.util.List;

public class ConflictCheckDialog extends Dialog {

    private final ShiftScheduleService service;
    private final ComboBox<Option<Integer>> department;

    public ConflictCheckDialog(ShiftScheduleService service, List<Option<Integer>> departmentOptions) {
        this.service = service;

        setHeaderTitle("檢測下月排班");

        department = new ComboBox<>("部門");
        department.setItemLabelGenerator(Option::getName);
        department.setItems(departmentOptions);

        add(department);

        Button confirmButton = new Button("檢測", event -> checkConflicts());
        Button cancelButton = new Button("關閉", event -> close());

        getFooter().add(confirmButton, cancelButton);
    }

    public void openDialogWithParameter(Option<Integer> department) {
        this.department.setValue(department);
        open();
    }

    private void checkConflicts() {
        if (department.getValue() == null) {
            Notification.show("請選擇部門");
            return;
        }

//        boolean success = service.checkShiftSchedules(department.getValue().getValue());
//        String message = success ? "檢測成功" : "檢測出錯誤";
//        Notification.show(message);
//        if (success) {
//            close();
//        }
    }
}
