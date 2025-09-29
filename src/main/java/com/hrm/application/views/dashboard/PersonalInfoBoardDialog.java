package com.hrm.application.views.dashboard;

import com.hrm.application.entity.UserInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.Arrays;
import java.util.List;

public class PersonalInfoBoardDialog  extends Dialog {

    Binder<UserInfo> binder = new BeanValidationBinder<>(UserInfo.class);
    TextField skype = new TextField("skype帳號");
    TextField telegram = new TextField("telegram帳號");
    TextField emergencyContact = new TextField("緊急聯絡人");
    TextField address = new TextField("通訊地址");
    TextField relationship = new TextField("關系。緊急聯絡人與員工的關系，例如父母、配偶、朋友等");
    TextField emergencyContactPhone = new TextField("緊急聯絡人電話");
    TextField emergencyContactAddress = new TextField("緊急連絡人通訊地址");
    TextField registeredAddress = new TextField("戶籍地址");


    public PersonalInfoBoardDialog() {
        setComponentSize();
        add(getContent());
        getFooter().add(createButtonsLayout());
        binder.bindInstanceFields(this);

    }

    private VerticalLayout getContent() {
        VerticalLayout content = new VerticalLayout();
        content.setWidth("50em");
        content.add(
                getLayoutLine(skype, telegram)
                , getLayoutLine(emergencyContact, relationship)
                , getLayoutLine(emergencyContactPhone, address)
                , getLayoutLine(emergencyContactAddress, registeredAddress)
        );
        return content;
    }

    private HorizontalLayout getLayoutLine(Component... fields) {
        HorizontalLayout layout = new HorizontalLayout(fields);
        layout.setWidthFull();
        return layout;
    }

    private void setComponentSize() {
        List<TextField> fields = Arrays.asList(
                skype, telegram, emergencyContact, address, relationship, emergencyContactPhone
                , emergencyContactAddress, registeredAddress
        );
        fields.forEach(field -> {
            field.setWidthFull();
        });
    }

    public void setPersonalInfo(UserInfo userInfo) {
        binder.setBean(userInfo);
    }

    private Component createButtonsLayout() {
        Button save = new Button("儲存");
        Button close = new Button("取消");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        addDialogCloseActionListener(event -> fireEvent(new CloseEvent(this)));
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }


    public static abstract class PersonalInfoBoardDialogEvent extends ComponentEvent<PersonalInfoBoardDialog> {
        private final UserInfo userInfo;

        protected PersonalInfoBoardDialogEvent(PersonalInfoBoardDialog source, UserInfo userInfo) {
            super(source, false);
            this.userInfo = userInfo;
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }
    }

    public static class SaveEvent extends PersonalInfoBoardDialogEvent {
        SaveEvent(PersonalInfoBoardDialog source, UserInfo userInfo) {
            super(source, userInfo);
        }
    }

    public static class CloseEvent extends PersonalInfoBoardDialogEvent {
        CloseEvent(PersonalInfoBoardDialog source) {
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
