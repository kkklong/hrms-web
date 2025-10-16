package com.hrm.application.views.shift.shiftSchedule2;

import com.hrm.application.entity.ShiftType;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShiftTypeSelectDialog extends Dialog {

    private final LocalDate date;
    private final RadioButtonGroup<ShiftType> rbg = new RadioButtonGroup<>();
    private final Button save = new Button("儲存");
    private final Button cancel = new Button("取消");

    public ShiftTypeSelectDialog(LocalDate date,
                                 List<ShiftType> allTypes,
                                 ShiftType current) {
        this.date = date;
        setHeaderTitle(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 班別選擇");

        rbg.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        rbg.setWidthFull();
        rbg.setItems(allTypes);
        rbg.setItemLabelGenerator(ShiftType::getShiftName);
        if (current != null) {
            rbg.setValue(current);
        }

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> {
            ShiftType selected = rbg.getValue();
            fireEvent(new SaveEvent(this, date, selected));
            close();
        });
        cancel.addClickListener(e -> {
            fireEvent(new CloseEvent(this, date));
            close();
        });

        HorizontalLayout buttons = new HorizontalLayout(cancel, save);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout body = new VerticalLayout(rbg);
        body.setPadding(false);
        body.setSpacing(false);

        add(body);
        getFooter().add(buttons);
    }

    // --- Events ---

    public static abstract class ShiftTypeSelectDialogEvent extends ComponentEvent<ShiftTypeSelectDialog> {
        private final LocalDate date;

        protected ShiftTypeSelectDialogEvent(ShiftTypeSelectDialog source, LocalDate date) {
            super(source, false);
            this.date = date;
        }
        public LocalDate getDate() { return date; }
    }

    public static class SaveEvent extends ShiftTypeSelectDialogEvent {
        private final ShiftType shiftType;
        public SaveEvent(ShiftTypeSelectDialog source, LocalDate date, ShiftType shiftType) {
            super(source, date);
            this.shiftType = shiftType;
        }
        public ShiftType getShiftType() { return shiftType; }
    }

    public static class CloseEvent extends ShiftTypeSelectDialogEvent {
        public CloseEvent(ShiftTypeSelectDialog source, LocalDate date) {
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
