package com.hrm.application.layout;

import com.hrm.application.menu.MenuItem;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.server.VaadinRequest;
import org.apache.commons.lang3.StringUtils;

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
    }

    protected void addDrawerContent() {
    }

    protected abstract void createMenuEntries(SideNav menuBuilder);

    protected void addMenu(SideNav navigation, Class<? extends Component> clazz) {
        MenuItem item = clazz.getAnnotation(MenuItem.class);
        String caption = item != null ? item.label() : String.join(" ", StringUtils.splitByCharacterTypeCamelCase(clazz.getSimpleName()));

        navigation.addItem(new SideNavItem(caption, clazz));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
//        UI.getCurrent().getPage().setTitle("::: FullCalendar Demo :::");
    }
}