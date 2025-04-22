package com.hrm.application.util;

import com.hrm.application.entity.UserInfo;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionUtil {

    private static final String KEY_TOKEN = "Cookie";
    private static final String KEY_USER_INFO = "currentEmployee";

    public static void setToken(String accessToken) {
        VaadinSession.getCurrent().setAttribute(KEY_TOKEN, accessToken);
    }

    public static String getToken() {
        return (String) VaadinSession.getCurrent().getAttribute(KEY_TOKEN);
    }

    public static void setUserInfo(UserInfo info) {
        VaadinSession.getCurrent().setAttribute(KEY_USER_INFO, info);
    }

    public static UserInfo getUserInfo() {
        return (UserInfo) VaadinSession.getCurrent().getAttribute(KEY_USER_INFO);
    }

    public static void cleanSession() {
        setToken(null);
        setUserInfo(null);
    }

}
