package com.hrm.application.layout;

import com.hrm.application.entity.UpdatePassword;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class UpdatePasswordDialog extends Dialog {

    PasswordField oldPassword = new PasswordField("舊密碼");

    PasswordField newPassword = new PasswordField("新密碼");

    PasswordField confirmPassword = new PasswordField("確認新密碼");

    Button save = new Button("儲存");

    Button close = new Button("取消");

    Binder<UpdatePassword> binder = new BeanValidationBinder<>(UpdatePassword.class);

    VerticalLayout vt = new VerticalLayout();

    public UpdatePasswordDialog() {
        addClassName("password-dialog");
        binder.bindInstanceFields(this);
        oldPassword.setWidth("25em");
        newPassword.setWidth("25em");
        confirmPassword.setWidth("25em");

        vt.add(oldPassword,
                newPassword,
                confirmPassword);
        add(vt);
        getFooter().add(createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, close);
    }


    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    public void setPassword(UpdatePassword updatePassword) {
        binder.setBean(updatePassword);
    }

    public static abstract class UpdatePasswordDialogEvent extends ComponentEvent<UpdatePasswordDialog> {
        private final UpdatePassword updatePassword;

        protected UpdatePasswordDialogEvent(UpdatePasswordDialog source, UpdatePassword updatePassword) {
            super(source, false);
            this.updatePassword = updatePassword;
        }

        public UpdatePassword getUpdatePassword() {
            return updatePassword;
        }
    }

    public static class SaveEvent extends UpdatePasswordDialogEvent {
        SaveEvent(UpdatePasswordDialog source, UpdatePassword updatePassword) {
            super(source, updatePassword);
        }
    }

    public static class CloseEvent extends UpdatePasswordDialogEvent {
        CloseEvent(UpdatePasswordDialog source) {
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
