package com.hrm.application.views.leave.leaveType;

import com.hrm.application.entity.Employee;
import com.hrm.application.entity.LeaveSpecialRecord;
import com.hrm.application.model.Option;
import com.hrm.application.model.Validator;
import com.hrm.application.service.LeaveTypeService;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LeaveTypeDialog extends Dialog {
    private final LeaveTypeService service;
    private final List<Option<String>> salaryStandardList;
    private final Map<String, Option<String>> salaryStandardMap;

    private List<Option<String>> leaveTypeList;
    private Map<String, Option<String>> leaveTypeMap;
    private List<Option<Integer>> departmentList;
    private Map<Integer, Option<Integer>> departmentMap;
    private List<Option<Integer>> employeeList;
    private Map<Integer, Option<Integer>> employeeMap;

    Select<Option<String>> leaveTypes = new Select<>();
    TextField leaveTypesText = new TextField("假別");
    Select<Option<Integer>> departmentName = new Select<>();
    Select<Option<Integer>> nickName = new Select<>();

    TextField minLeaveUnit = new TextField("最低請假單位/小時");
    TextField maxLeaveDays = new TextField("期間可請假天數");
    Select<Option<String>> salaryStandard = new Select<>();
    DateTimePicker startDate = new DateTimePicker("生效時間");
    DateTimePicker endDate = new DateTimePicker("失效時間");

    RadioButtonGroup<Boolean> fullAttendanceBonus = new RadioButtonGroup<>("是否計算全勤");
    RadioButtonGroup<Boolean> continuousLeave = new RadioButtonGroup<>("是否需要連續請假");
    RadioButtonGroup<Boolean> advanceApplication = new RadioButtonGroup<>("是否需要事前提出");
    RadioButtonGroup<Boolean> attachmentRequired = new RadioButtonGroup<>("是否需要證明檔案");

    TextArea description = new TextArea("備註");

    HorizontalLayout section1 = new HorizontalLayout();
    HorizontalLayout section2 = new HorizontalLayout();
    HorizontalLayout section3 = new HorizontalLayout();
    HorizontalLayout section4 = new HorizontalLayout();

    Button save = new Button("儲存");
    Button cancel = new Button("取消");

    Binder<LeaveSpecialRecord> binder = new BeanValidationBinder<>(LeaveSpecialRecord.class);
    private Registration departmentValueChangeRegistration;


    public LeaveTypeDialog(LeaveTypeService service) {

        this.service = service;
        this.salaryStandardList = service.querySalaryStandard();
        this.salaryStandardMap = ToolUtil.transToMap(salaryStandardList, Option::getValue);

        setData();

        addClassName("leave-type-dialog");

        addContent();
        getFooter().add(createButtonsLayout());

        binder.bind(leaveTypes, this::setLeaveType, (r, t) -> r.setLeaveTypes(t.getValue()));
        binder.bind(departmentName, t -> departmentMap.get(t.getDepartmentId()), (r, d) -> r.setDepartmentId(d.getValue()));
        binder.bind(nickName, t -> employeeMap.get(t.getEmployeeId()), (r, e) -> r.setEmployeeId(e.getValue()));
        binder.bind(salaryStandard, record -> salaryStandardMap.get(record.getSalaryStandard()),
                (record, option) -> record.setSalaryStandard(Optional.ofNullable(option).map(Option::getValue).orElse(null)));
        binder.bindInstanceFields(this);

    }

    private void setData() {
        leaveTypeList = service.getNonAutoScheduledLeaves();
        departmentList = service.getDepartmentOptionList();
        employeeList = service.getEmployeeOptionList();
        leaveTypeMap = ToolUtil.transToMap(leaveTypeList, Option::getValue);
        departmentMap = ToolUtil.transToMap(departmentList, Option::getValue);
        employeeMap = ToolUtil.transToMap(employeeList, Option::getValue);
    }

    private void addContent() {

        leaveTypes.setLabel("假別");
        leaveTypes.setItemLabelGenerator(Option::getName);
        leaveTypes.setItems(leaveTypeList);
        leaveTypes.setWidth("12em");
        leaveTypesText.setWidth("12em");
        leaveTypesText.setReadOnly(true);
        departmentName.setWidth("12em");
        departmentName.setLabel("部門");
        departmentName.setItems(departmentList);
        departmentName.setItemLabelGenerator(Option::getName);
        nickName.setWidth("12em");
        nickName.setLabel("員工");
        nickName.setItemLabelGenerator(Option::getName);
        nickName.setItems(employeeList);
        setDialogView(false, false);

        minLeaveUnit.setWidth("12em");
        maxLeaveDays.setWidth("12em");
        salaryStandard.setWidth("12em");
        salaryStandard.setItems(salaryStandardList);
        salaryStandard.setLabel("計薪標準");
        salaryStandard.setItemLabelGenerator(option -> Objects.requireNonNull(option).getName());
        startDate.setWidth("20em");
        endDate.setWidth("20em");
        startDate.setStep(Duration.ofMinutes(30));
        endDate.setStep(Duration.ofMinutes(30));

        fullAttendanceBonus.setItems(true, false);
        fullAttendanceBonus.setItemLabelGenerator(value -> value ? "是" : "否");
        continuousLeave.setItems(true, false);
        continuousLeave.setItemLabelGenerator(value -> value ? "是" : "否");
        advanceApplication.setItems(true, false);
        advanceApplication.setItemLabelGenerator(value -> value ? "是" : "否");
        attachmentRequired.setItems(true, false);
        attachmentRequired.setItemLabelGenerator(value -> value ? "是" : "否");

        description.setWidth("40em");

        section1.add(leaveTypes, leaveTypesText, departmentName, nickName);
        section2.add(minLeaveUnit, maxLeaveDays, salaryStandard);
        section3.add(startDate, endDate);
        section4.add(fullAttendanceBonus, continuousLeave, advanceApplication, attachmentRequired);

        VerticalLayout formLayout = new VerticalLayout(section1, section2, section3, section4, description);
        add(formLayout);
    }

    public void setCreateItemListenerAction() {
        if (departmentValueChangeRegistration != null) {
            departmentValueChangeRegistration.remove(); // 移除舊的監聽器
        }
        departmentValueChangeRegistration = departmentName.addValueChangeListener(e ->
                setEmployeeItems(Optional.ofNullable(e.getValue()).map(Option::getValue).orElse(null))
        );
    }

    private void setEmployeeItems(Integer departmentId) {
        if (departmentId != null) {
            nickName.setItems(employeeList.stream().filter(e -> e.getReferenceValue().equals(departmentId))
                    .collect(Collectors.toList()));
        } else {
            nickName.setItems(new ArrayList<>());
        }
    }

    public void removeCreateItemListenerAction() {
        if (departmentValueChangeRegistration != null) {
            departmentValueChangeRegistration.remove();
            departmentValueChangeRegistration = null;
            nickName.setItems(employeeList);
        }
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        cancel.addClickListener(event -> fireEvent(new LeaveTypeDialog.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, cancel);
    }

    private void validateAndSave() {
        LeaveSpecialRecord record = binder.getBean();

        // 1. 驗證規則
        List<Validator> validators = Arrays.asList(
                new Validator("請選擇假別",
                        () -> record.getLeaveTypes() == null || record.getLeaveTypes().isBlank()),
                new Validator("請選擇員工",
                        () -> record.getEmployeeId() == null),
                new Validator("請輸入最低請假單位",
                        () -> record.getMinLeaveUnit() == null),
                new Validator("請正確輸入最低請假單位",
                        () -> record.getMinLeaveUnit() != null && Float.compare(record.getMinLeaveUnit(), 0f) <= 0),
                new Validator("請輸入可請假天數",
                        () -> record.getMaxLeaveDays() == null),
                new Validator("請正確輸入可請假天數",
                        () -> record.getMaxLeaveDays() != null && record.getMaxLeaveDays() <= 0),
                new Validator("請選擇計薪標準",
                        () -> record.getSalaryStandard() == null),
                new Validator("請設定生效時間",
                        () -> record.getStartDate() == null),
                new Validator("請設定失效時間",
                        () -> record.getEndDate() == null),
                new Validator("生效時間不能晚於失效時間",
                        () -> record.getStartDate() != null && record.getEndDate() != null
                                && record.getStartDate().isAfter(record.getEndDate())),
                new Validator("請選擇是否計算全勤",
                        () -> record.getFullAttendanceBonus() == null),
                new Validator("請選擇是否需要連續請假",
                        () -> record.getContinuousLeave() == null),
                new Validator("請選擇是否需要事前提出",
                        () -> record.getAdvanceApplication() == null),
                new Validator("請選擇是否需要證明文件",
                        () -> record.getAttachmentRequired() == null)
        );

        // 2. 驗證並收集錯誤
        List<String> errors = validators.stream()
                .filter(Validator::isInvalid)
                .map(Validator::getErrorMessage)
                .collect(Collectors.toList());

        // 3. 顯示錯誤或儲存
        if (!errors.isEmpty()) {
            Notification.show(String.join("； ", errors), 5000, Notification.Position.BOTTOM_START);
        } else if (binder.isValid()) {
            fireEvent(new SaveEvent(this, record));
        }
    }

    private Option<String> setLeaveType(LeaveSpecialRecord record) {
        String name = record.getChineseName();
        if (name != null) {
            leaveTypesText.setValue(name);
        }
        return leaveTypeMap.get(record.getLeaveTypes());
    }

    public void setLeaveSpecialRecords(LeaveSpecialRecord leaveSpecialRecord) {
        binder.setBean(leaveSpecialRecord);
    }

    public void setDialogView(boolean isCreate, boolean isEdit) {
        leaveTypes.setVisible(isCreate);
        leaveTypesText.setVisible(isEdit);
        departmentName.setReadOnly(!isCreate);
        nickName.setReadOnly(!isCreate);
    }

    public static abstract class LeaveTypeDialogEvent extends ComponentEvent<LeaveTypeDialog> {

        private final LeaveSpecialRecord leaveSpecialRecord;

        protected LeaveTypeDialogEvent(LeaveTypeDialog source, LeaveSpecialRecord leaveSpecialRecord) {
            super(source, false);
            this.leaveSpecialRecord = leaveSpecialRecord;
        }

        public LeaveSpecialRecord getLeaveSpecialRecords() {
            return leaveSpecialRecord;
        }
    }

    public static class SaveEvent extends LeaveTypeDialogEvent {
        SaveEvent(LeaveTypeDialog source, LeaveSpecialRecord leaveSpecialRecord) {
            super(source, leaveSpecialRecord);
        }
    }

    public static class CloseEvent extends LeaveTypeDialogEvent {
        CloseEvent(LeaveTypeDialog source) {
            super(source, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
