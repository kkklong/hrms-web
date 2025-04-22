package com.hrm.application.layout;

import com.hrm.application.entity.UserInfo;
import com.hrm.application.menu.MenuItem;
import com.hrm.application.service.AccountService;
import com.hrm.application.views.DashboardView;
import com.hrm.application.views.LoginView;
import com.hrm.application.views.calendar.CalendarView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.theme.lumo.Lumo;
import org.apache.commons.lang3.StringUtils;

public class MainLayout extends AbstractLayout {

    AccountService accountService;
    UserInfo currentEmployee;


    public MainLayout(AccountService accountService) {
        this.accountService = accountService;
        getUserInfo();
        addHeaderContent();
        addDrawerContent();
    }

    @Override
    protected void createMenuEntries(SideNav nav) {
        addMenu(nav, DashboardView.class);
        addMenu(nav, CalendarView.class);
    }

    @Override
    protected void addMenu(SideNav navigation, Class<? extends Component> clazz) {
        MenuItem item = clazz.getAnnotation(MenuItem.class);

        String caption = item != null
                ? item.label()
                : String.join(" ", StringUtils.splitByCharacterTypeCamelCase(clazz.getSimpleName()));

        Component iconComponent = item != null
                ? item.icon().create()
                : null;

        SideNavItem sideNavItem = (iconComponent != null)
                ? new SideNavItem(caption, clazz, iconComponent)
                : new SideNavItem(caption, clazz);

        navigation.addItem(sideNavItem);
    }

    @Override
    protected void addHeaderContent() {
        Component title = generateTitle("HRM System Demo");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)");
        title.getStyle().set("font-weight", "bold");

        Avatar avatarBasic = new Avatar();
        addToNavbar(true, new DrawerToggle(), title, logoutAnchor(), createNotificationBell(), avatarBasic, accountInfo());
    }

    private void getUserInfo() {
        currentEmployee = accountService.getCurrentUser();
        if (currentEmployee == null) {
            UI.getCurrent().getPage().setLocation("/login");
        }

    }

    private Anchor logoutAnchor() {
        String route = RouteConfiguration.forSessionScope().getUrl(LoginView.class);
        Anchor logout = new Anchor(route, "Logout");
        logout.setMinWidth("4em");
        logout.getStyle().set("margin-right", "2px");
        logout.getStyle().set("font-weight", "bold");
        logout.getElement().addEventListener("click", event -> {
            boolean result = this.accountService.logout();
            if (result) {
                Notification.show("登出成功!");
            } else {
                Notification.show("登出失敗!");
            }
        });
        return logout;
    }

    private TextField accountInfo() {
        TextField account = new TextField();
        account.setReadOnly(true);
        account.addClassName("account-textfield");
        account.getStyle().set("--vaadin-input-field-readonly-border", "none");
        if (currentEmployee != null) {
            account.setValue(currentEmployee.getAccount());
            account.setHelperText(currentEmployee.getPosition());
        }
        return  account;
    }

    private Span createNotificationBell() {
        Span notificationBell = new Span(createBellIconWrapper());
        notificationBell.getStyle().set("margin-right", "5px");

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
                ".</span>"));

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