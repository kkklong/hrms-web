package com.hrm.application.views.leave.leaveTemplate;

import com.hrm.application.entity.LeaveSpecialRecordTemplate;
import com.hrm.application.model.Option;
import com.hrm.application.service.LeaveTemplateService;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Map;

public class LeaveTemplateDialog extends Dialog {
    private final List<Option<String>> salaryStandardList;
    private final Map<String, Option<String>> salaryStandardMap;
    private final List<Option<String>> calculationPeriodList;
    private final Map<String, Option<String>> calculationPeriodMap;

    TextField leaveTypes = new TextField("假別(年資)");

    TextField minLeaveUnit = new TextField("最低請假單位/小時");

    TextField maxLeaveDays = new TextField("期間可請假天數上限");

    Select<Option<String>> salaryStandard = new Select<>();
    Select<Option<String>> calculationPeriod = new Select<>();

    RadioButtonGroup<Boolean> fullAttendanceBonus = new RadioButtonGroup<>("是否計算全勤");
    RadioButtonGroup<Boolean> continuousLeave = new RadioButtonGroup<>("是否要求連續請假");
    RadioButtonGroup<Boolean> advanceApplication = new RadioButtonGroup<>("是否事前提出");
    RadioButtonGroup<Boolean> attachmentRequired = new RadioButtonGroup<>("是否需要證明檔案");

    HorizontalLayout section1 = new HorizontalLayout();
    HorizontalLayout section2 = new HorizontalLayout();
    HorizontalLayout section3 = new HorizontalLayout();
    HorizontalLayout section4 = new HorizontalLayout();
    Button save = new Button("儲存");
    Button cancel = new Button("取消");
    Binder<LeaveSpecialRecordTemplate> binder = new BeanValidationBinder<>(LeaveSpecialRecordTemplate.class);

    public LeaveTemplateDialog(LeaveTemplateService service) {

        this.salaryStandardList = service.querySalaryStandard();
        this.salaryStandardMap = ToolUtil.transToMap(salaryStandardList, Option::getValue);
        this.calculationPeriodList = service.queryCalculationPeriod();
        this.calculationPeriodMap = ToolUtil.transToMap(calculationPeriodList, Option::getValue);

        addClassName("leave-template-dialog");

        setComponentWidth("12em", minLeaveUnit, maxLeaveDays, salaryStandard, calculationPeriod, fullAttendanceBonus, continuousLeave, advanceApplication, attachmentRequired);
        leaveTypes.setWidth("24em");
        leaveTypes.setReadOnly(true);

        binder.forField(leaveTypes).bind(service::getLeaveName, null);

        salaryStandard.setItems(salaryStandardList);
        salaryStandard.setLabel("計薪標準");
        salaryStandard.setItemLabelGenerator(Option::getName);
        binder.bind(salaryStandard, template -> salaryStandardMap.get(template.getSalaryStandard()), (template, option) -> template.setSalaryStandard(option.getValue()));

        calculationPeriod.setItems(calculationPeriodList);
        calculationPeriod.setLabel("計算期間類型");
        calculationPeriod.setItemLabelGenerator(Option::getName);
        binder.bind(calculationPeriod, template -> calculationPeriodMap.get(template.getCalculationPeriod()), (template, option) -> template.setCalculationPeriod(option.getValue()));


        fullAttendanceBonus.setItems(true, false);
        fullAttendanceBonus.setItemLabelGenerator(value -> value ? "是" : "否");

        continuousLeave.setItems(true, false);
        continuousLeave.setItemLabelGenerator(value -> value ? "是" : "否");

        advanceApplication.setItems(true, false);
        advanceApplication.setItemLabelGenerator(value -> value ? "是" : "否");

        attachmentRequired.setItems(true, false);
        attachmentRequired.setItemLabelGenerator(value -> value ? "是" : "否");


        section1.add(minLeaveUnit, maxLeaveDays);
        section2.add(salaryStandard, calculationPeriod);
        section3.add(fullAttendanceBonus, continuousLeave);
        section4.add(advanceApplication, attachmentRequired);


        VerticalLayout vt = new VerticalLayout();
        vt.add(leaveTypes, section1, section2, section3, section4);

        add(vt);
        getFooter().add(createButtonsLayout());

        binder.bindInstanceFields(this);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        cancel.addClickListener(event -> fireEvent(new LeaveTemplateDialog.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, cancel);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            LeaveSpecialRecordTemplate leaveSpecialRecordTemplate = binder.getBean();
            fireEvent(new SaveEvent(this, leaveSpecialRecordTemplate));
        }
    }

    public void setLeaveSpecialRecordsTemplate(LeaveSpecialRecordTemplate leaveSpecialRecordTemplate) {
        binder.setBean(leaveSpecialRecordTemplate);
    }

    public static abstract class LeaveTemplateDialogEvent extends ComponentEvent<LeaveTemplateDialog> {

        private final LeaveSpecialRecordTemplate leaveSpecialRecordTemplate;

        protected LeaveTemplateDialogEvent(LeaveTemplateDialog source, LeaveSpecialRecordTemplate leaveSpecialRecordTemplate) {
            super(source, false);
            this.leaveSpecialRecordTemplate = leaveSpecialRecordTemplate;
        }

        public LeaveSpecialRecordTemplate getLeaveSpecialRecordsTemplate() {
            return leaveSpecialRecordTemplate;
        }
    }

    public static class SaveEvent extends LeaveTemplateDialogEvent {
        SaveEvent(LeaveTemplateDialog source, LeaveSpecialRecordTemplate leaveSpecialRecordTemplate) {
            super(source, leaveSpecialRecordTemplate);
        }
    }

    public static class CloseEvent extends LeaveTemplateDialogEvent {
        CloseEvent(LeaveTemplateDialog source) {
            super(source, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

    private void setComponentWidth(String width, HasSize... components) {
        for (HasSize component : components) {
            component.setWidth(width);
        }
    }

}
