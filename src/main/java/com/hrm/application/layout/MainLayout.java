package com.hrm.application.layout;

import com.hrm.application.entity.UserInfo;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.service.AccountService;
import com.hrm.application.views.HomePageView;
import com.hrm.application.views.LoginView;
import com.hrm.application.views.calendar.CalendarView;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
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

@CssImport("./app-layout-styles.css")
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
        addMenu(nav, HomePageView.class);
        addMenu(nav, CalendarView.class);
    }

    @Override
    protected void addMenu(SideNav navigation, Class<? extends Component> clazz) {
        MenuRouter item = clazz.getAnnotation(MenuRouter.class);

        String caption = item != null
                ? item.label()
                : String.join(" ", StringUtils.splitByCharacterTypeCamelCase(clazz.getSimpleName()));

        Component iconComponent = item != null
                ? item.icon().create()
                : VaadinIcon.COG_O.create();

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
        addToNavbar(true, new DrawerToggle(), title, createNotificationBell(), accountMenu(), accountInfo());
    }

    private void getUserInfo() {
        currentEmployee = accountService.getCurrentUser();
        if (currentEmployee == null) {
            UI.getCurrent().getPage().setLocation("/login");
        }
    }

    private MenuBar accountMenu () {
        MenuBar accountMenu = new MenuBar();
        accountMenu.setOpenOnHover(true);
        MenuItem profileItem = accountMenu.addItem(new Avatar());
        SubMenu accountSubMenu = profileItem.getSubMenu();
        accountSubMenu.addItem("Change Password");
        accountSubMenu.add(new Hr());
        accountSubMenu.addItem("Logout", event -> {
            boolean result = this.accountService.logout();
            if (result) {
                UI.getCurrent().navigate(LoginView.class);
                Notification.show("登出成功!");
            } else {
                Notification.show("登出失敗!");
            }
        });
        return accountMenu;
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
//        wrapper.getStyle().set("position", "relative");
        wrapper.getStyle().set("display", "inline-block");

        return wrapper;
    }

    @Override
    protected void addDrawerContent() {
        H4 header = new H4("NaviItem");
        header.addClassName("header");

        VerticalLayout footer = new VerticalLayout();

        Div footerText = new Div(new Html("<span>   &#169 Using the Vaadin 24.2.3" +
                "</span>"));

        footer.addClassName("footer");
        footer.add(themeToggle(), footerText);

        SideNav nav = new SideNav();
        createMenuEntries(nav);
        addToDrawer(header, new Hr(), new Scroller(nav), footer);
    }

    protected Button themeToggle() {
        Button themeToggle = new Button("Toggle light theme");
        themeToggle.setWidthFull();
        // 點擊後切換
        themeToggle.addClickListener(click -> {
            ThemeList currentThemeList = UI.getCurrent().getElement().getThemeList();
            if (currentThemeList.contains(Lumo.DARK)) {
                currentThemeList.remove(Lumo.DARK);
                themeToggle.setText("Toggle dark theme");
            } else {
                currentThemeList.add(Lumo.DARK);
                themeToggle.setText("Toggle light theme");
            }
        });
        return themeToggle;
    }
}