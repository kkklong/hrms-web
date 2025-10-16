package com.hrm.application.views.shift.managerTool;

import com.hrm.application.model.Option;
import com.hrm.application.service.ShiftScheduleService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;

import java.util.List;

public class CloseShiftScheduleDialog extends Dialog {

    private final ShiftScheduleService service;
    private final ComboBox<Option<Integer>> departmentComboBox;

    public CloseShiftScheduleDialog(ShiftScheduleService service, List<Option<Integer>> departmentOptions) {
        this.service = service;

        setHeaderTitle("關閉下月排班");

        departmentComboBox = new ComboBox<>("部門");
        departmentComboBox.setItemLabelGenerator(Option::getName);
        departmentComboBox.setItems(departmentOptions);

        add(departmentComboBox);

        Button confirmButton = new Button("關閉排班", event -> closeShiftSchedules());
        Button cancelButton = new Button("取消", event -> close());

        getFooter().add(confirmButton, cancelButton);
    }

    public void openDialogWithParameter(Option<Integer> department) {
        this.departmentComboBox.setValue(department);
        open();
    }

    private void closeShiftSchedules() {
//        if (departmentComboBox.getValue() == null) {
//            Notification.show("請選擇部門");
//            return;
//        }
//
//        boolean success = service.closeNextMonthShiftSchedules(departmentComboBox.getValue().getValue());
//        String message = success ? "已成功關閉排班" : "關閉排班失敗";
//        Notification.show(message);
//
//        if (success) {
//            close();
//        }
    }
}
