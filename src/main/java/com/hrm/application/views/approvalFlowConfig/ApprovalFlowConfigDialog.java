package com.hrm.application.views.approvalFlowConfig;

import com.hrm.application.entity.ApprovalFlowConfig;
import com.hrm.application.entity.Employee;
import com.hrm.application.model.Option;
import com.hrm.application.service.ApprovalFlowConfigService;
import com.hrm.application.views.employee.EmployeeDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.List;

import static com.hrm.application.util.ToolUtil.dataConverter;

public class ApprovalFlowConfigDialog extends Dialog {
    private final ApprovalFlowConfigService service;
    private Binder<ApprovalFlowConfig> binder = new BeanValidationBinder<>(ApprovalFlowConfig.class);


    private final ComboBox<Option<String>> scopeType = new ComboBox<>("範圍類型");
    private final TextField scopeValue = new TextField("範圍值");
    private final TextField flowJson = new TextField("簽核流程");
    private final ComboBox<Integer> active = new ComboBox<>("狀態");

    private List<Option<String>> scopeTypeOpts = List.of();
    private List<Option<String>> companyOpts = List.of();
    private List<Option<String>> reviewOpts = List.of();
    private List<Option<String>> intervalOpts = List.of();
    private List<Option<Integer>> departmentOpts = List.of();
    private List<Option<Integer>> employeeOpts = List.of();

    private Button save = new Button("儲存");
    private Button update = new Button("更新");
    private Button delete = new Button("刪除");
    private Button close = new Button("關閉");


    // 簽核流程
    private final CheckboxGroup<Option<String>> lt24 = new CheckboxGroup<>();
    private final CheckboxGroup<Option<String>> ge24 = new CheckboxGroup<>();



    public ApprovalFlowConfigDialog(ApprovalFlowConfigService service
                              , List<Option<String>> scopeTypeList
                              , List<Option<String>> companyList
                              , List<Option<String>> reviewList
                              , List<Option<String>> intervalList
                              , List<Option<Integer>> departmentList
                              , List<Option<Integer>> employeeList
    ) {
        this.service = service;
        this.scopeTypeOpts = scopeTypeList;
        this.companyOpts = companyList;
        this.reviewOpts = reviewList;
        this.intervalOpts = intervalList;
        this.departmentOpts = departmentList;
        this.employeeOpts = employeeList;

        setData();
        add(getContent());
        getFooter().add(createButtonsLayout());

        binder.forField(scopeType)
                .withConverter(dataConverter(scopeTypeList))
                .bind(ApprovalFlowConfig::getScopeType, ApprovalFlowConfig::setScopeType);
        binder.bindInstanceFields(this);

    }

    private void setData() {
        scopeType.setItems(scopeTypeOpts);
        scopeType.setItemLabelGenerator(Option::getName);
        active.setItems(0, 1); // 選項是 0 和 1
        active.setItemLabelGenerator(i -> i == 0 ? "停用" : "啟用");
    }

    private VerticalLayout getContent() {
        return new VerticalLayout(
                new HorizontalLayout(scopeType, scopeValue),
                new HorizontalLayout(lt24),
                new HorizontalLayout(ge24),
                new HorizontalLayout(active)
        );
    }

    public void setApprovalFlowDialog(ApprovalFlowConfig afcfg) {
        binder.setBean(afcfg);
    }

    public void setDialogView(Boolean isCreate) {
        save.setVisible(isCreate);
        update.setVisible(!isCreate);
        delete.setVisible(!isCreate);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

//        save.addClickListener(event -> validateAndSave());
//        update.addClickListener(event -> validateAndUpdate());
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, update, delete, close);
    }






    public static abstract class ApprovalFlowConfigDialogEvent extends ComponentEvent<ApprovalFlowConfigDialog> {
        private final ApprovalFlowConfig afcfg;

        protected ApprovalFlowConfigDialogEvent(ApprovalFlowConfigDialog source, ApprovalFlowConfig afcfg) {
            super(source, false);
            this.afcfg = afcfg;
        }

        public ApprovalFlowConfig getApprovalFlowConfig() {
            return afcfg;
        }
    }

    public static class SaveEvent extends ApprovalFlowConfigDialog.ApprovalFlowConfigDialogEvent {
        SaveEvent(ApprovalFlowConfigDialog source, ApprovalFlowConfig afcfg) {
            super(source, afcfg);
        }
    }

    public static class UpdateEvent extends ApprovalFlowConfigDialog.ApprovalFlowConfigDialogEvent {
        UpdateEvent(ApprovalFlowConfigDialog source, ApprovalFlowConfig afcfg) {
            super(source, afcfg);
        }
    }

    public static class DeleteEvent extends ApprovalFlowConfigDialog.ApprovalFlowConfigDialogEvent {
        DeleteEvent(ApprovalFlowConfigDialog source, ApprovalFlowConfig afcfg) {
            super(source, afcfg);
        }

    }

    public static class CloseEvent extends ApprovalFlowConfigDialog.ApprovalFlowConfigDialogEvent {
        CloseEvent(ApprovalFlowConfigDialog source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<ApprovalFlowConfigDialog.DeleteEvent> listener) {
        return addListener(ApprovalFlowConfigDialog.DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<ApprovalFlowConfigDialog.SaveEvent> listener) {
        return addListener(ApprovalFlowConfigDialog.SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<ApprovalFlowConfigDialog.UpdateEvent> listener) {
        return addListener(ApprovalFlowConfigDialog.UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<ApprovalFlowConfigDialog.CloseEvent> listener) {
        return addListener(ApprovalFlowConfigDialog.CloseEvent.class, listener);
    }
}
