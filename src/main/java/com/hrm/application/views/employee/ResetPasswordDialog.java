package com.hrm.application.views.employee;

import com.hrm.application.entity.ResetPassword;
import com.hrm.application.service.EmployeeService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;


public class ResetPasswordDialog extends Dialog {
    PasswordField newPassword = new PasswordField("新密碼");
    Button save = new Button("確認");
    Button close = new Button("取消");
    Binder<ResetPassword> binder = new BeanValidationBinder<>(ResetPassword.class);
    VerticalLayout vt = new VerticalLayout();
    private final EmployeeService employeeService;

    public ResetPasswordDialog(EmployeeService employeeService) {
        this.employeeService = employeeService;
        addClassName("setPassword-dialog");
        binder.bindInstanceFields(this);

        newPassword.setWidth("24em");
        vt.add(newPassword);

        add(vt);
        getFooter().add(createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            ResetPassword resetPassword = binder.getBean();
//            boolean success = employeeService.setPassword(setPassword);
//            if (success) {
//                Notification.show("密碼重設成功");
//                close();
//            } else {
//                Notification.show("密碼重設失敗");
//            }
        }
    }

    public void setPassword(Integer employeeId) {
        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setId(employeeId);
        binder.setBean(resetPassword);
    }


    public static abstract class ResetPasswordDialogEvent extends ComponentEvent<ResetPasswordDialog> {
        private final ResetPassword resetPassword;

        protected ResetPasswordDialogEvent(ResetPasswordDialog source, ResetPassword resetPassword) {
            super(source, false);
            this.resetPassword = resetPassword;
        }

        public ResetPassword getResetPassword() {
            return resetPassword;
        }
    }

    public static class SaveEvent extends ResetPasswordDialogEvent {
        SaveEvent(ResetPasswordDialog source, ResetPassword resetPassword) {
            super(source, resetPassword);
        }
    }

    public static class CloseEvent extends ResetPasswordDialogEvent {
        CloseEvent(ResetPasswordDialog source) {
            super(source, null);
        }
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
