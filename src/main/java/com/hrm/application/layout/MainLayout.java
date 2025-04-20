package com.hrm.application.layout;

import com.hrm.application.service.AccountService;
import com.hrm.application.views.DashboardView;
import com.hrm.application.views.calendar.CalendarView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.theme.lumo.Lumo;

public class MainLayout extends AbstractLayout {

    AccountService accountService;

    public MainLayout(AccountService accountService) {
        this.accountService = accountService;

        //檢查登入狀態
        if (!accountService.checkIsLogin()) {
            UI.getCurrent().getPage().setLocation("/login");
            return;
        }
    }


    @Override
    protected void createMenuEntries(SideNav nav) {
        addMenu(nav, DashboardView.class);
        addMenu(nav, CalendarView.class);
    }

    @Override
    protected void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
//        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        Component title = generateTitle("HRM System Demo");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)");
        title.getStyle().set("font-weight", "bold");

        Avatar avatarBasic = new Avatar();
        TextField account = new TextField();
        account.setValue("KKK");
        account.setReadOnly(true);
        account.getStyle().set("--vaadin-input-field-readonly-border", "none");
        Span bell = createNotificationBell();
        addToNavbar(true, toggle, title, bell, avatarBasic, account);
    }

    private Span createNotificationBell() {
        Span notificationBell = new Span(createBellIconWrapper());
        notificationBell.getElement().getThemeList().add("badge");
//        notificationBell.addClickListener(click -> {
//            UI.getCurrent().navigate("notification");
//            updateNotificationCount();
//        });
//        updateNotificationCount();
        return notificationBell;
    }

    private Div createBellIconWrapper() {
        Icon bellIcon = new Icon(VaadinIcon.BELL_O);
        bellIcon.getStyle().set("margin-right", "5px");

        Div wrapper = new Div(bellIcon);
        wrapper.getStyle().set("position", "relative");
        wrapper.getStyle().set("display", "inline-block");

        return wrapper;
    }

    @Override
    protected void addDrawerContent() {
        H4 header = new H4("NaviItem");
        header.addClassName("header");

        VerticalLayout footer = new VerticalLayout();

        Div footerText = new Div(new Html("<span>Using the Vaadin 24.2.3" +
                "More information can be found <a href=\"https://xxxx\" target=\"_blank\">here</a>.</span>"));

        footer.addClassName("footer");
        footer.add(themeToggle(), footerText);

        SideNav nav = new SideNav();
        createMenuEntries(nav);
        addToDrawer(header, new Scroller(nav), footer);
    }

    protected Button themeToggle() {
        Button themeToggle = new Button("Toggle dark theme", click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });
        themeToggle.setWidthFull();
        return themeToggle;
    }
}