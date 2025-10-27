package com.hrm.application.views.shift.shiftSchedule3;

import com.hrm.application.component.DragAndSelect;
import com.hrm.application.entity.Notice;
import com.hrm.application.entity.ShiftType;
import com.hrm.application.model.Option;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.util.NotificationUtil;
import com.hrm.application.views.employee.EmployeeDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpSelectDialog extends Dialog {
    List<ShiftSchedulesQueryVO> items = new ArrayList<>();
    private Grid<ShiftSchedulesQueryVO> grid = new Grid<>(ShiftSchedulesQueryVO.class, false);

    public EmpSelectDialog(List<ShiftSchedulesQueryVO> allRows) {
        items = allRows;
        setWidth("20em");
        add(getContent());
        getFooter().add(createButtonsLayout());
    }

    private VerticalLayout getContent() {
        VerticalLayout vt = new VerticalLayout();
        vt.setSizeFull();
        vt.addClassNames("grid-content");

        grid.setSizeFull();
        grid.setAllRowsVisible(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setItems(items);
        grid.asMultiSelect().select(items);
        grid.addColumn(ShiftSchedulesQueryVO::getNickName).setHeader("選擇人員 [全選/取消]").setAutoWidth(true);
        vt.add(getTitle(), grid);
        return vt;
    }

    private HorizontalLayout getTitle() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H5 title = new H5("選擇調整班表的員工");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private Component createButtonsLayout(){
        Button save = new Button("儲存");
        Button close = new Button("取消");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        save.addClickListener(event -> {
            List<ShiftSchedulesQueryVO> selectRows = grid.getSelectedItems().stream().toList();
            if(selectRows.size() == 0) {
                NotificationUtil.error("未選擇要儲存班表的員工");
            } else {
                fireEvent(new SaveEvent(this, selectRows));
                close();
            }
        });
        close.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
            close();
        });

        FormLayout buttons = new FormLayout(save, close);
        buttons.setWidthFull();
//        buttons.setColspan(save, 1);
//        buttons.setColspan(close, 1);
//        buttons.setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("240px", 2)
//        );
        return buttons;
    }

    // --- Events ---

    public static abstract class EmpSelectDialogEvent extends ComponentEvent<EmpSelectDialog> {
        private final List<ShiftSchedulesQueryVO> rows;

        protected EmpSelectDialogEvent(EmpSelectDialog source, List<ShiftSchedulesQueryVO> rows) {
            super(source, false);
            this.rows = rows;
        }
        public List<ShiftSchedulesQueryVO> getRows() { return rows; }
    }

    public static class SaveEvent extends EmpSelectDialogEvent {

        public SaveEvent(EmpSelectDialog source, List<ShiftSchedulesQueryVO> rows) {
            super(source, rows);
        }
    }

    public static class CloseEvent extends EmpSelectDialogEvent {
        public CloseEvent(EmpSelectDialog source) {
            super(source, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<EmpSelectDialog.SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<EmpSelectDialog.CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
