package com.hrm.application.calendar;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.stefan.fullcalendar.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarViewToolbar extends MenuBar {
    public static final List<Timezone> SOME_TIMEZONES =
            Arrays.asList(Timezone.UTC, new Timezone(ZoneId.of("America/Los_Angeles")), new Timezone(ZoneId.of("Japan")), new Timezone(ZoneId.of("Asia/Taipei")));
    private final FullCalendar calendar;
    private Button buttonDatePicker;
    private HasComponents calendarParent;
    private MenuItem viewSelector;
    private CalendarView selectedView = CalendarViewImpl.DAY_GRID_MONTH;


    @Builder
    public CalendarViewToolbar(FullCalendar calendar) {
        this.calendar = calendar;
        initMenuBar();

    }

    protected void initMenuBar() {
        initDateItems();
//        initGeneralSettings();
        initViewSelector();
    }

    private void initDateItems() {
        DatePicker gotoDate = new DatePicker();
        gotoDate.addValueChangeListener(event1 -> calendar.gotoDate(event1.getValue()));
        gotoDate.setWeekNumbersVisible(true);
        gotoDate.getElement().getStyle().set("visibility", "hidden");
        gotoDate.getElement().getStyle().set("position", "fixed");
        gotoDate.setWidth("0px");
        gotoDate.setHeight("0px");
        addItem(VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
        buttonDatePicker = new Button();
        buttonDatePicker.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        buttonDatePicker.getElement().appendChild(gotoDate.getElement());
        buttonDatePicker.addClickListener(event -> gotoDate.open());
        buttonDatePicker.setWidthFull();
        addItem(buttonDatePicker);
        addItem(VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
        addItem("Today", e -> calendar.today());
    }

    public void updateInterval(LocalDate intervalStart) {
        if (buttonDatePicker != null && selectedView != null) {
            updateIntervalLabel(buttonDatePicker, selectedView, intervalStart);
        }
    }

    void updateIntervalLabel(HasText intervalLabel, CalendarView view, LocalDate intervalStart) {
        String text = "--";
        Locale locale = calendar.getLocale();
        String pattern = view != null && view.getDateTimeFormatPattern() != null ? view.getDateTimeFormatPattern() : "MMMM yyyy";
        text = intervalStart.format(DateTimeFormatter.ofPattern(pattern).withLocale(locale));
        intervalLabel.setText(text);
    }

    private SubMenu initGeneralSettings() {
        SubMenu subMenu = addItem("Settings").getSubMenu();
        //Theme
        Checkbox themeSelector = new Checkbox("Use Lumo Theme");
        themeSelector.setValue(calendar.hasThemeVariant(FullCalendarVariant.LUMO));
        themeSelector.addValueChangeListener(event -> {
            boolean useLumo = event.getValue();
            if (useLumo) {
                calendar.addThemeVariants(FullCalendarVariant.LUMO);
            } else {
                calendar.removeThemeVariants(FullCalendarVariant.LUMO);
            }
        });

        VerticalLayout verticalLayout = new VerticalLayout(
                themeSelector
        );
        verticalLayout.setSpacing(false);
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(true);
        verticalLayout.setSizeUndefined();
        verticalLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        subMenu.add(verticalLayout);

        subMenu.addItem("Detach/Attach Calendar", event -> {
            if (calendar.getParent().isPresent()) {
                calendarParent = (HasComponents) calendar.getParent().get();
                calendarParent.remove(calendar);
            } else if (calendarParent != null) {
                calendarParent.add(calendar);
            }
        });

        return subMenu;
    }

    private void initViewSelector() {
        List<CalendarView> calendarViews =  Arrays.stream(CalendarViewImpl.values())
                .filter(view -> view == CalendarViewImpl.DAY_GRID_MONTH
                        || view == CalendarViewImpl.MULTI_MONTH)
                .collect(Collectors.toList());

        calendarViews.sort(Comparator.comparing(CalendarView::getName));

        viewSelector = addItem("View: " + getViewName(selectedView));
        SubMenu subMenu = viewSelector.getSubMenu();
        calendarViews.stream()
                .sorted(Comparator.comparing(this::getViewName))
                .forEach(view -> {
                    String viewName = getViewName(view);
                    subMenu.addItem(viewName, event -> {
                        calendar.changeView(view);
                        viewSelector.setText("View: " + viewName);
                        selectedView = view;
                    });
                });
    }

    private String getViewName(CalendarView view) {
        String name = null /*customViewNames.get(view)*/;
        if (name == null) {
            name = StringUtils.capitalize(String.join(" ", StringUtils.splitByCharacterTypeCamelCase(view.getClientSideValue())));
        }

        return name;
    }

    public void updateSelectedView(CalendarView view) {
        if (viewSelector != null) {
            viewSelector.setText("View: " + getViewName(view));
        }
        selectedView = view;
    }

}
