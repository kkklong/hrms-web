package com.hrm.application.views.shift.shiftSchedule3;

import com.hrm.application.entity.ShiftType;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiftSelectDialog extends Dialog {

    private final LocalDate date;
    private final RadioButtonGroup<ShiftType> rbg = new RadioButtonGroup<>();
    private final Button save = new Button("確認");
    private final Button cancel = new Button("取消");
    private final TextArea remark = new TextArea();

    public ShiftSelectDialog(LocalDate date,
                             List<ShiftType> allTypes,
                             ShiftType current, String remarkS) {
        this.date = date;
        setHeaderTitle(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 班別選擇");

        rbg.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        rbg.setWidthFull();
        rbg.setItems(allTypes);
        rbg.setItemLabelGenerator(ShiftType::getShiftName);
        if (current != null) {
            rbg.setValue(current);
        }
        this.remark.setValue(remarkS);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> {
            ShiftType selected = rbg.getValue();
            fireEvent(new SaveEvent(this, date, selected, remark.getValue()));
            close();
        });
        cancel.addClickListener(e -> {
            fireEvent(new CloseEvent(this, date));
            close();
        });

        HorizontalLayout buttons = new HorizontalLayout(cancel, save);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Tab tab1 = new Tab("排班設定");
        Tab tab2 = new Tab("備註說明");

        VerticalLayout tab1Content = getLayoutLine(rbg);
        VerticalLayout tab2Content = getLayoutLine(remark);

        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(tab1, tab1Content);
        tabsToPages.put(tab2, tab2Content);

        Tabs tabs = new Tabs(tab1, tab2);
        tabs.setWidthFull();

        Div pages = new Div(tab1Content, tab2Content);
        pages.setWidthFull();
        pages.setHeightFull();

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            if (selectedPage != null) selectedPage.setVisible(true);
        });

        tab1Content.setVisible(true);
        tab2Content.setVisible(false);

        VerticalLayout body = new VerticalLayout(tabs, pages);
        body.setPadding(false);
        body.setSpacing(false);
        body.setWidth("15em");
        body.setHeight("25em");
        body.addClassName("grid-content");
        add(body);
        getFooter().add(buttons);
        addClassName("background-plan");
    }

    private VerticalLayout getLayoutLine(Component... fields) {
        VerticalLayout layout = new VerticalLayout(fields);
//        layout.setWidth("30em");
        return layout;
    }

    // --- Events ---

    public static abstract class ShiftTypeSelectDialogEvent extends ComponentEvent<ShiftSelectDialog> {
        private final LocalDate date;

        protected ShiftTypeSelectDialogEvent(ShiftSelectDialog source, LocalDate date) {
            super(source, false);
            this.date = date;
        }
        public LocalDate getDate() { return date; }
    }

    public static class SaveEvent extends ShiftTypeSelectDialogEvent {
        private final ShiftType shiftType;
        private final String remark;

        public SaveEvent(ShiftSelectDialog source, LocalDate date, ShiftType shiftType, String remark) {
            super(source, date);
            this.shiftType = shiftType;
            this.remark = remark;
        }
        public ShiftType getShiftType() { return shiftType; }
        public String getRemark() { return remark; }

    }

    public static class CloseEvent extends ShiftTypeSelectDialogEvent {
        public CloseEvent(ShiftSelectDialog source, LocalDate date) {
            super(source, date);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
