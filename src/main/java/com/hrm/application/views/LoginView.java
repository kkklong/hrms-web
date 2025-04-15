package com.hrm.application.views;

import com.hrm.application.service.AccountService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;

@Route("login")
@PageTitle("登入 | 人力資源管理系統")
public class LoginView extends VerticalLayout {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoginView.class);
    private final LoginForm login = new LoginForm();
    private final AccountService service;

    public LoginView(AccountService service) {
        this.service = service;
//        if (service.checkIsLogin()) {
//            UI.getCurrent().getPage().setLocation("/");
//            return;
//        }
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        LoginI18n i18n = LoginI18n.createDefault();

        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle("登入");
        i18nForm.setUsername("帳號");
        i18nForm.setPassword("密碼");
        i18nForm.setSubmit("登入");
        i18nForm.setForgotPassword("忘記密碼");
        i18n.setForm(i18nForm);


        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("帳號或密碼錯誤");
        i18nErrorMessage.setMessage(
                "檢查帳號和密碼是否正確，然後重試。");
        i18n.setErrorMessage(i18nErrorMessage);

        i18nErrorMessage.setUsername("請輸入帳號");
        i18nErrorMessage.setPassword("請輸入密碼");

        add(new H1("三也科技 人力資源管理系統"));
        login.setI18n(i18n);
        add(login);

        login.addLoginListener(e -> {
            boolean isAuthenticated = !(this.service.login(e.getUsername(), e.getPassword())).isBlank();
            if (isAuthenticated) {
                Notification.show("登入成功!");
                // 登錄成功後的頁面跳轉邏輯，導向根目錄
                UI.getCurrent().navigate("");
            } else {
                login.setError(true);
            }
        });
    }

}

