package com.hrm.application.views.dashboard;

import com.hrm.application.entity.UpdatePassword;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.service.AccountService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class PersonalInfoBoard extends VerticalLayout {
    AccountService service;
//    UpdatePasswordDialog dialog;
    PersonalInfoBoardDialog dialogInfo;
    UserInfo currentEmployee;
    private FormLayout details;
    private boolean isSensitiveInfoVisible = false;

    public PersonalInfoBoard(AccountService service) {

        this.service = service;
        addClassName("personalInfo-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        add(titleConfigure(), configureDiv());
        configureDialog();
        this.addClassName("background-plan");

        setSizeFull();
    }

    private HorizontalLayout titleConfigure() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("UserInfo");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private VerticalLayout configureDiv() {
        VerticalLayout board = new VerticalLayout();
        board.addClassName("personalInfo-board");

//        H2 title = new H2("個人資料");
//        title.getStyle().set("text-align", "left");
//        title.addClassName(LumoUtility.Padding.MEDIUM);

        Button editInfoButton = new Button("編輯個人資料");
        editInfoButton.addClickListener(click -> editPersonalInfo());
        Button hiddenInfoButton = new Button("顯示/隱藏資訊");
        hiddenInfoButton.addClickListener(click -> hiddenInfo());
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(editInfoButton, hiddenInfoButton);
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.add(buttonLayout);
        headerLayout.setAlignItems(Alignment.BASELINE);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN); // 按鈕右側

        // 使用FormLayout來排列詳細資訊
        details = new FormLayout();
        details.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        updateUserInfo();
        updateDetails();
        board.add(headerLayout, details);
        return board;
    }

    private String getSensitiveValue(Object value) {
        if (isSensitiveInfoVisible) {
            return getValueOrEmpty(value);
        } else {
            return "***";
        }
    }

    private void updateDetails() {
        details.removeAll();
        addDetail(details, "姓名", getValueOrEmpty(currentEmployee.getFullName()), 1);
        addDetail(details, "英文名", getValueOrEmpty(currentEmployee.getNickName()), 1);
//        addDetail(details, "公司", getValueOrEmpty(service.getCompanyName(currentEmployee.getCompany())), 1);
        addDetail(details, "部門", getValueOrEmpty(currentEmployee.getDepartmentName()), 1);
        addDetail(details, "員工編號", getValueOrEmpty(currentEmployee.getEmployeeNumber()), 1);
        addDetail(details, "身份證字號", getSensitiveValue(currentEmployee.getIdNumber()), 1);
        addDetail(details, "職位", getValueOrEmpty(currentEmployee.getPosition()), 1);
        addDetail(details, "入職時間", getValueOrEmpty(currentEmployee.getEntryDate()), 1);
        addDetail(details, "性別", getValueOrEmpty(currentEmployee.getGender()), 1);
        addDetail(details, "生日", getSensitiveValue(currentEmployee.getBirthday()), 1);
        addDetail(details, "最高學歷", getSensitiveValue(currentEmployee.getHighestEducationLevel()), 1);
        addDetail(details, "電話", getSensitiveValue(currentEmployee.getPhone()), 1);

        addDetail(details, "帳號", getValueOrEmpty(currentEmployee.getAccount()), 2);
        addDetail(details, "skype帳號", getValueOrEmpty(currentEmployee.getSkype()), 1);
        addDetail(details, "telegram帳號", getValueOrEmpty(currentEmployee.getTelegram()), 1);
        addDetail(details, "信箱", getValueOrEmpty(currentEmployee.getEmail()), 2);
        addDetail(details, "通訊地址", getSensitiveValue(currentEmployee.getAddress()), 2);
        addDetail(details, "戶籍地址", getSensitiveValue(currentEmployee.getRegisteredAddress()), 2);
        addDetail(details, "緊急聯絡人", getSensitiveValue(currentEmployee.getEmergencyContact()), 2);
        addDetail(details, "與緊急聯絡人關系", getSensitiveValue(currentEmployee.getRelationship()), 1);
        addDetail(details, "緊急聯絡人電話", getSensitiveValue(currentEmployee.getEmergencyContactPhone()), 1);
        addDetail(details, "緊急連絡人通訊地址", getSensitiveValue(currentEmployee.getEmergencyContactAddress()), 2);
    }

    private void addDetail(FormLayout layout, String label, String value, int colspan) {
        HorizontalLayout itemLayout = new HorizontalLayout();
        itemLayout.setWidthFull();
        itemLayout.add(new Span(label), new Span(value));
        itemLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        itemLayout.addClassName("detail-item");
        layout.add(itemLayout);
        layout.setColspan(itemLayout, colspan);
    }

    private String getValueOrEmpty(Object value) {
        return value != null ? value.toString() : "";
    }


    private void configureDialog() {
//        dialog = new UpdatePasswordDialog();
//        dialog.addSaveListener(this::savePassword);
//        dialog.addCloseListener(e -> closeEditor());

        dialogInfo = new PersonalInfoBoardDialog();
        dialogInfo.addSaveListener(this::updatePersonalInfo);
        dialogInfo.addCloseListener(e -> closeInfoEditor());
    }

    private void closeEditor() {
//        dialog.setPassword(null);
//        dialog.close();
        removeClassName("editing");
    }

    private void closeInfoEditor() {
        dialogInfo.setPersonalInfo(null);
        dialogInfo.close();
        removeClassName("editing");
    }

//    private void savePassword(UpdatePasswordDialog.SaveEvent event) {
//        UpdatePassword updatePassword = event.getUpdatePassword();
//        boolean success = service.updatePassword(updatePassword);
//        if (success) {
//            Notification.show("變更成功");
//            closeEditor();
//        }
//    }

//    private void openDialog() {
//        dialog.setPassword(new UpdatePassword());
//        dialog.open();
//        addClassName("editing");
//    }

    private void editPersonalInfo() {
        dialogInfo.setPersonalInfo(service.getCurrentUser());
        dialogInfo.open();
        addClassName("editing");
    }

    private void hiddenInfo() {
        isSensitiveInfoVisible = !isSensitiveInfoVisible;
        updateDetails();
    }

    private void updatePersonalInfo(PersonalInfoBoardDialog.SaveEvent event) {
        UserInfo userInfo = event.getUserInfo();
        boolean success = service.updateUserInfo(userInfo);
        if (success) {
            Notification.show("更新成功");
            updateUserInfo();
            updateDetails();
            closeInfoEditor();
        } else {
            Notification.show("更新失敗");
        }
    }

    private void updateUserInfo() {
        currentEmployee = service.getCurrentUser();
    }
}
