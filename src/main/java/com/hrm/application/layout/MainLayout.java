package com.hrm.application.layout;

import com.hrm.application.demo.TestView;
import com.hrm.application.entity.Menu;
import com.hrm.application.entity.UpdatePassword;
import com.hrm.application.entity.UserInfo;
import com.hrm.application.menu.MenuLink;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.service.AccountService;
import com.hrm.application.service.MenuService;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.views.HomePageView;
import com.hrm.application.views.LoginView;
import com.hrm.application.views.approvalFlowConfig.ApprovalFlowConfigView;
import com.hrm.application.views.calendar.FullCalendar;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@PreserveOnRefresh
@CssImport("./app-layout-styles.css")
public class MainLayout extends AbstractLayout {

    AccountService accountService;
    MenuService menuService;
    UserInfo currentEmployee;
    UpdatePasswordDialog dialog;
    List<SideNavItem> userMenu;


    public MainLayout(AccountService accountService, MenuService menuService) {
        this.accountService = accountService;
        this.menuService = menuService;

        getUserInfo();
        configureDialog();
        setTheme();
        getUserMenuList();

        addHeaderContent();
        addDrawerContent();
    }

    @Override
    protected void createMenuEntries(SideNav nav) {
        addMenu(nav, HomePageView.class);
        addMenu(nav, FullCalendar.class);
//        addMenu(nav, ApprovalFlowConfigView.class);
//        addMenu(nav, TestView.class);

//        addMenu(nav, EmployeeView.class);
//        addMenu(nav, DepartmentView.class);
//        addMenu(nav, ShiftSchedulesQueryView.class);
//        addMenu(nav, NoticeView.class);
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
        Component title = generateTitle();
        title.getStyle().set("font-size", "var(--lumo-font-size-l)");
        title.getStyle().set("font-weight", "bold");
        addToNavbar(true, new DrawerToggle(), title, createNotificationBell(), accountMenu(), accountInfo());
    }

    protected Component generateTitle() {
        Span span = new Span();

        span.setWidthFull();
        span.getStyle()
                .set("margin-left", "var(--app-layout-menu-toggle-button-padding)")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("text-align", "center")
                .set("font-size", "var(--lumo-font-size-l)");
// 畫面size縮小時, 變更title顯示的文字
        span.getElement().executeJs(
                """
                const titleEl = this;
                function updateTitleText() {
                    if (window.matchMedia('(max-width: 40em)').matches) {
                        titleEl.textContent = 'HRM';
                    } else {
                        titleEl.textContent = 'HRM System Demo';
                    }
                }
                updateTitleText();
                window.addEventListener('resize', updateTitleText);
                """
        );
        return span;
    }

    private void getUserInfo() {
        currentEmployee = accountService.getCurrentUser();
        if (currentEmployee == null) {
            UI.getCurrent().getPage().setLocation("/login");
        }
    }

    private MenuBar accountMenu () {
        MenuBar accountMenu = new MenuBar();
//        accountMenu.setOpenOnHover(true);
        MenuItem profileItem = accountMenu.addItem(new Avatar());
        SubMenu accountSubMenu = profileItem.getSubMenu();
        accountSubMenu.addItem("Change Password", event -> {
            openDialog();
        });
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

    private void configureDialog() {
        dialog = new UpdatePasswordDialog();
        dialog.addSaveListener(this::updatePassword);
        dialog.addCloseListener(e -> closeEditor());
    }

    private void openDialog() {
        dialog.setPassword(new UpdatePassword());
        dialog.open();
    }

    private void closeEditor() {
        dialog.setPassword(null);
        dialog.close();
    }

    private void updatePassword(UpdatePasswordDialog.SaveEvent event) {
        UpdatePassword updatePassword = event.getUpdatePassword();
        boolean success = accountService.updatePassword(updatePassword);
        if (success) {
            Notification.show("變更成功");
            closeEditor();
        }
    }

    private TextField accountInfo() {
        TextField account = new TextField();
        account.setWidth("8em");
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

        VerticalLayout layout = new VerticalLayout();
        if(userMenu != null) {
            for (SideNavItem item : userMenu) {
                layout.add(item);
            }
        }
        layout.setHeightFull();
        addToDrawer(header, new Hr(), nav, layout, footer);
    }

    private void getUserMenuList() {
        List<Menu> menuList = menuService.getMenuList();
        if (menuList == null || menuList.isEmpty()) {
            userMenu = new ArrayList<>();
        }
        userMenu = menuList.stream().map(this::createNavItem).collect(Collectors.toList());
    }

    private SideNavItem createNavItem(Menu menu) {

        MenuLink link = MenuLink.getMenuByCode(menu.getCode());

        SideNavItem item = createNavItem(menu.getText(), link.getPage(), link.getIcon());
        if (menu.getData() == null) {
            if (link.getPage() == null) {
                item.setClassName("menu-item-disable");
            }
            return item;
        }
        for (Menu subMenu : menu.getData()) {
            item.addItem(createNavItem(subMenu));
        }
        return item;
    }

    private SideNavItem createNavItem(String text, Class<? extends Component> page, VaadinIcon icon) {

        SideNavItem item = new SideNavItem(text);
        if (page != null) {
            item.setPath(page);
        }
        item.setPrefixComponent(icon != null ? icon.create() : VaadinIcon.COG.create());
        return item;
    }

    // -------- Theme setup --------
    protected Button themeToggle() {
        Button themeToggle = new Button("Toggle dark theme");
        themeToggle.setWidthFull();
        // 點擊後切換
        themeToggle.addClickListener(click -> {
            ThemeList currentThemeList = UI.getCurrent().getElement().getThemeList();
            if (currentThemeList.contains(Lumo.DARK)) {
                setThemeCookie("light");
                currentThemeList.remove(Lumo.DARK);
                themeToggle.setText("Toggle dark theme");
            } else {
                setThemeCookie("dark");
                currentThemeList.add(Lumo.DARK);
                themeToggle.setText("Toggle light theme");
            }
        });
        return themeToggle;
    }

    private void setThemeCookie(String theme) {
        Stream.of(VaadinService.getCurrentResponse())
                .filter(response -> response instanceof VaadinResponse)
                .findFirst()
                .ifPresent(response -> {
                    Cookie cookie = new Cookie("user-theme", theme);
                    cookie.setPath("/");
                    cookie.setMaxAge(60 * 60 * 24 * 365); // 一年
                    ((VaadinResponse) response).addCookie(cookie);
                });
    }

    private String getThemeFromCookie() {
        HttpServletRequest request = (HttpServletRequest) VaadinService.getCurrentRequest();
        if (request != null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("user-theme".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setTheme() {
        String theme = getThemeFromCookie();
        if ("dark".equals(theme)) {
            UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        ui.access(() -> {
            try {
//                getUserInfo();
//                configureDialog();
//                setTheme();
//                getUserMenuList();
            } catch (Exception e) {
                NotificationUtil.error("載入資料失敗：" + e.getMessage());
            }
        });
    }
}