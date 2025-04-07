package com.hrm.application.layout;

import com.hrm.application.menu.MenuItem;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.stefan.fullcalendar.FullCalendar;

import java.util.Locale;

@CssImport("./app-layout-styles.css")
@SuppressWarnings("rawtypes")
public abstract class AbstractLayout extends AppLayout implements AfterNavigationObserver {
    public static final String ADDON_VERSION = "6.2.2-SNAPSHOT";
    private static final long serialVersionUID = -7479612679602267287L;

    @SuppressWarnings("unchecked")
    public AbstractLayout() {
        selectCurrentLocale();

        setPrimarySection(Section.DRAWER);
        addHeaderContent();
        addDrawerContent();
    }


    protected void selectCurrentLocale() {
        Locale locale = (Locale) VaadinRequest.getCurrent().getWrappedSession().getAttribute("locale");
        if (locale == null) {
            locale = UI.getCurrent().getLocale();
            VaadinRequest.getCurrent().getWrappedSession().setAttribute("locale", locale);
        } else
            UI.getCurrent().setLocale(locale);
    }

    protected Component generateTitle(String title) {
        Span span = new Span(title);

        span.setWidthFull();
        span.getStyle()
                .set("margin-left", "var(--app-layout-menu-toggle-button-padding)")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("text-align", "center")
                .set("font-size", "var(--lumo-font-size-l)");
//                .set("font-weight", "bold");

        return span;
    }

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

    protected abstract void createMenuEntries(SideNav menuBuilder);

    protected void addMenu(SideNav navigation, Class<? extends Component> clazz) {
        MenuItem item = clazz.getAnnotation(MenuItem.class);
        String caption = item != null ? item.label() : String.join(" ", StringUtils.splitByCharacterTypeCamelCase(clazz.getSimpleName()));

        navigation.addItem(new SideNavItem(caption, clazz));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        UI.getCurrent().getPage().setTitle("::: FullCalendar Demo :::");
    }
}