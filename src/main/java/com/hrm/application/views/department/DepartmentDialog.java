package com.hrm.application.views.department;

import com.hrm.application.component.ConfirmDialog;
import com.hrm.application.entity.Department;
import com.hrm.application.entity.Option;
import com.hrm.application.entity.ShiftType;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Map;

public class DepartmentDialog extends Dialog {

    private TextField departmentParent = new TextField("母部門");
    private TextField company = new TextField("公司");
    private TextField departmentName = new TextField("部門名稱");
    private TextField description = new TextField("描述");
    private ComboBox<Option<Integer>> managerId = new ComboBox<>("部門主管");
    private ComboBox<ShiftType> workType = new ComboBox<>("預設班別");

    private TextField everyDayMorningCount = new TextField("早班最少上班人數");
    private TextField everyDayAfternoonCount = new TextField("午班最少上班人數");
    private TextField everyDayNightCount = new TextField("晚班最少上班人數");
    private TextField reviewConfig = new TextField("審核流程");

    private Button save = new Button("儲存");
    private Button update = new Button("更新");
    private Button delete = new Button("刪除");
    private Button close = new Button("取消");
    ConfirmDialog confirmDialog;

    // Other fields omitted
    Binder<Department> binder = new BeanValidationBinder<>(Department.class);

    public DepartmentDialog(List<Option<Integer>> employeeList, Map<Integer, Option<Integer>> employeeMap, List<ShiftType> shiftTypeList, Map<String, ShiftType> shiftTypeMap) {
        addClassName("department-dialog");
        VerticalLayout vt = new VerticalLayout();
        setUpComponentSize();
        managerId.setItems(employeeList);
        managerId.setItemLabelGenerator(Option::getName);
        workType.setItems(shiftTypeList);
        workType.setItemLabelGenerator(ShiftType::getShiftName);

        everyDayMorningCount.setRequired(true);
        everyDayAfternoonCount.setRequired(true);
        everyDayNightCount.setRequired(true);

        HorizontalLayout minCountHt = new HorizontalLayout();
        minCountHt.add(everyDayMorningCount, everyDayAfternoonCount, everyDayNightCount);
        minCountHt.setWidthFull();
        minCountHt.setFlexGrow(1, everyDayMorningCount, everyDayAfternoonCount, everyDayNightCount);

        vt.add(departmentName,
                description,
                managerId,
                workType,
                minCountHt
        );
        add(vt);
        getFooter().add(createButtonsLayout());

        binder.bind(managerId, d -> employeeMap.get(d.getManagerId()), (d, s) -> d.setManagerId(s.getValue()));
        binder.bind(workType, d -> shiftTypeMap.get(d.getWorkType()), (d, s) -> d.setWorkType(s.getShiftKey()));

        binder.bindInstanceFields(this);
    }

    private void setUpComponentSize() {
        departmentName.setWidth("40em");
        description.setWidth("40em");
        managerId.setWidth("40em");
        workType.setWidth("40em");
        reviewConfig.setWidthFull();
        everyDayMorningCount.setWidth("10em");
        everyDayAfternoonCount.setWidth("10em");
        everyDayNightCount.setWidth("10em");
    }

    private Component createButtonsLayout() {
        confirmDialog = new ConfirmDialog();
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        update.addClickListener(event -> validateAndUpdate());
//        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        delete.addClickListener(click -> {
            confirmDialog.openDialogWithParameter("確認執行刪除?", "刪除");
        });
        confirmDialog.setConfirmAction(() -> {
            fireEvent(new DeleteEvent(this, binder.getBean()));
            confirmDialog.close();
        });
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, update, delete, close);
    }

    public void setDialogView (Boolean isCreate) {
        save.setVisible(isCreate);
        update.setVisible(!isCreate);
        delete.setVisible(!isCreate);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    private void validateAndUpdate() {
        if (binder.isValid()) {
            fireEvent(new UpdateEvent(this, binder.getBean()));
        }
    }

    public void setDepartment(Department department) {
        binder.setBean(department);
    }

    // Events
    public static abstract class DepartmentDialogEvent extends ComponentEvent<DepartmentDialog> {
        private final Department department;

        protected DepartmentDialogEvent(DepartmentDialog source, Department department) {
            super(source, false);
            this.department = department;
        }

        public Department getDepartment() {
            return department;
        }
    }

    public static class SaveEvent extends DepartmentDialogEvent {
        SaveEvent(DepartmentDialog source, Department department) {
            super(source, department);
        }
    }

    public static class UpdateEvent extends DepartmentDialogEvent {
        UpdateEvent(DepartmentDialog source, Department department) {
            super(source, department);
        }
    }

    public static class DeleteEvent extends DepartmentDialogEvent {
        DeleteEvent(DepartmentDialog source, Department department) {
            super(source, department);
        }
    }

    public static class CloseEvent extends DepartmentDialogEvent {
        CloseEvent(DepartmentDialog source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }


}

