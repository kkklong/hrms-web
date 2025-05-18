package com.hrm.application.views.shift;

import com.hrm.application.entity.ShiftType;
import com.hrm.application.model.vo.ShiftSchedulesDateTimeQueryVO;
import com.hrm.application.model.vo.ShiftSchedulesQueryVO;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ShiftSchedulesQueryDialog extends Dialog {
    private LocalDate selectedDate;
    private Map<LocalDate, ComboBox<ShiftType>> comboBoxes = new HashMap<>();
    private Map<LocalDate, TextField> remarkFieldsMap = new HashMap<>();
    private List<ShiftType> shiftTypeList;
    private Map<String, ShiftType> shiftTypeMap;
    private ShiftSchedulesQueryVO shiftSchedules;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");

    Button update = new Button("保存");
    Button close = new Button("關閉");

    Binder<ShiftSchedulesQueryVO> binder = new BeanValidationBinder<>(ShiftSchedulesQueryVO.class);

    public ShiftSchedulesQueryDialog(List<ShiftType> shiftTypeList, Map<String, ShiftType> shiftTypeMap, LocalDate selectedDate, ShiftSchedulesQueryVO shiftSchedules) {
        this.selectedDate = selectedDate;
        this.shiftTypeList = shiftTypeList;
        this.shiftTypeMap = shiftTypeMap;
        this.shiftSchedules = shiftSchedules;
        setBinder();
        add(createSelectionLayout(shiftSchedules));
        addClassName("background-plan");
        getFooter().add(createButtonsLayout());
    }

    private VerticalLayout createSelectionLayout(ShiftSchedulesQueryVO shiftSchedules) {
        if (shiftSchedules == null) {
            return new VerticalLayout();
        }
        comboBoxes.clear();

        // 主容器
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMinWidth("40em");
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        // 標題
        TextField countDetail = new TextField();
        countDetail.setReadOnly(true);
        countDetail.setWidth("20em");
        countDetail.getStyle().set("font-weight", "bold");
        countDetail.getStyle().set("--vaadin-input-field-readonly-border", "none");
        mainLayout.add(new HorizontalLayout(new H3("員工: " + shiftSchedules.getNickName() + " 排班"), countDetail));
        mainLayout.addClassName("background-plan");

        // 建立兩個 Tab
        Tab tab1 = new Tab("排班設定");
        Tab tab2 = new Tab("備註說明");

        // 每個 Tab 對應的內容
        Div tab1Content = createComboBoxLayout(shiftSchedules, countDetail);
        Div tab2Content = createRemarksLayout(shiftSchedules);

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

        mainLayout.add(tabs, pages);
        return mainLayout;
    }

    // 排班設定的layout
    private Div createComboBoxLayout(ShiftSchedulesQueryVO shiftSchedules, TextField countDetail) {
        Div wrapper = new Div();
        wrapper.setWidthFull();
        wrapper.setHeightFull();

        FormLayout contentLayout = new FormLayout();
        contentLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 7));
        contentLayout.setWidthFull();

        List<ShiftSchedulesDateTimeQueryVO> datesList = shiftSchedules.getSchedulesDates();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");

        int itemCount = 0;
        for (int i = 0; i < datesList.size(); i++) {
            LocalDate date = datesList.get(i).getShiftDate();

            if (i == 0) {
                int emptySlots = date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
                for (int j = 0; j < emptySlots; j++) {
                    contentLayout.add(new TextField());
                    itemCount++;
                }
            }

            ShiftSchedulesDateTimeQueryVO shiftSchedulesDateTime = datesList.get(i);
            ComboBox<ShiftType> shiftTypesField = new ComboBox<>();
            shiftTypesField.setWidthFull();
            shiftTypesField.setItems(shiftTypeList);
            shiftTypesField.setLabel(shiftSchedulesDateTime.getActionType() == 0
                    ? date.format(dateFormatter)
                    : date.format(dateFormatter) + " [R]");
            shiftTypesField.setReadOnly(shiftSchedulesDateTime.getActionType() == 1);
            shiftTypesField.getStyle().set("--vaadin-input-field-border-width", "1.5px");
            shiftTypesField.getElement().getStyle().set("font-size", "14px");
            shiftTypesField.getStyle().set("--vaadin-combo-box-overlay-width", "11em");
            shiftTypesField.setValue(shiftTypeMap.getOrDefault(shiftSchedulesDateTime.getShiftTypes(), null));
            shiftTypesField.setItemLabelGenerator(ShiftType::getShiftName);
            updateShiftTypeStyles(shiftTypesField, shiftTypesField.getValue(), countDetail);
            shiftTypesField.addValueChangeListener(e -> {
                updateShiftTypeStyles(shiftTypesField, e.getValue(), countDetail);
            });

            comboBoxes.put(date, shiftTypesField); // 更新後的shiftType存進全域變數裡
            contentLayout.add(shiftTypesField);
            itemCount++;

            if (itemCount % 14 == 0) {
                wrapper.add(contentLayout, new Hr());
                contentLayout = new FormLayout();
                contentLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 7));
            }
        }

        if (itemCount % 14 != 0) {
            wrapper.add(contentLayout);
        }

        return wrapper;
    }

    private void updateShiftTypeStyles(ComboBox<ShiftType> comboBox, ShiftType shiftType, TextField countDetail) {
        if (shiftType != null && shiftType.getShiftKey().contains("HOLIDAY")) {
            comboBox.removeClassName("shiftType-combo-shiftday");
            comboBox.addClassName("shiftType-combo-holiday");
        } else {
            comboBox.removeClassName("shiftType-combo-holiday");
            comboBox.addClassName("shiftType-combo-shiftday");
        }

        setBackgroundColor(comboBox, shiftType != null ? shiftType.getShiftColorCode() : null);

        countDetail.setValue(String.format("[休假日: %d] [例假日: %d] [國定假: %d]",
                getShiftTypeCount(comboBoxes, "REST"),
                getShiftTypeCount(comboBoxes, "REGULAR"),
                getShiftTypeCount(comboBoxes, "NATIONAL")));
    }

    // 備註設定的layout
    private Div createRemarksLayout(ShiftSchedulesQueryVO shiftSchedules) {
        Div wrapper = new Div();
        wrapper.setWidthFull();
        wrapper.setHeightFull();

        FormLayout contentLayout = new FormLayout();
        contentLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 7));
        contentLayout.setWidthFull();

        List<ShiftSchedulesDateTimeQueryVO> datesList = shiftSchedules.getSchedulesDates();
        int itemCount = 0;

        for (int i = 0; i < datesList.size(); i++) {
            LocalDate date = datesList.get(i).getShiftDate();

            if (i == 0) {
                int emptySlots = date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
                for (int j = 0; j < emptySlots; j++) {
                    contentLayout.add(new TextField());
                    itemCount++;
                }
            }

            ShiftSchedulesDateTimeQueryVO shiftSchedulesDateTime = datesList.get(i);
            TextField remarkField = new TextField();
            remarkField.setValue(Optional.ofNullable(shiftSchedulesDateTime.getRemark()).orElse(""));
//            remarkField.setReadOnly(true);
            remarkField.setWidthFull();
            remarkField.getStyle().set("font-size", "14px");
            remarkField.setLabel(shiftSchedulesDateTime.getActionType() == 0
                    ? date.format(dateFormatter)
                    : date.format(dateFormatter) + " [R]");
            contentLayout.add(remarkField);
            remarkFieldsMap.put(date, remarkField); // 更新後的remark存進全域變數裡
            itemCount++;

            if (itemCount % 14 == 0) {
                wrapper.add(contentLayout, new Hr());
                contentLayout = new FormLayout();
                contentLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 7));
            }
        }

        if (itemCount % 14 != 0) {
            wrapper.add(contentLayout);
        }

        return wrapper;
    }

    private void setBackgroundColor(HasStyle component, String backgroundColorCode) {
        String textColorCode = ToolUtil.getTextColorForHexBackground(backgroundColorCode);
        component.getStyle().set("--vaadin-input-field-background", backgroundColorCode);
        component.getStyle().set("--vaadin-input-field-value-color", textColorCode);
    }

    private long getShiftTypeCount (Map<LocalDate, ComboBox<ShiftType>> comboBoxes, String keyword) {
        return comboBoxes.values().stream()
                .map(ComboBox::getValue)
                .filter(Objects::nonNull)
                .filter(shiftType -> shiftType.getShiftKey().contains(keyword))
                .count();
    }

    private Component createButtonsLayout() {
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        close.addClickShortcut(Key.ESCAPE);

        update.addClickListener(event -> validateAndUpdate());
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        addDialogCloseActionListener(event -> fireEvent(new CloseEvent(this)));
        binder.addStatusChangeListener(e -> update.setEnabled(binder.isValid()));
        return new HorizontalLayout(update, close);
    }

    private void validateAndUpdate() {
        if (binder.isValid()) {
            // 遍歷 comboBoxes，將選擇的 ShiftType 更新回 shiftSchedules 的 schedulesDates 清單
            for (Map.Entry<LocalDate, ComboBox<ShiftType>> entry : comboBoxes.entrySet()) {
                LocalDate date = entry.getKey();
                ComboBox<ShiftType> shiftTypeComboBox = entry.getValue();
                ShiftType selectedShiftType = shiftTypeComboBox.getValue();

                // 找到對應日期的 ShiftSchedulesDateTimeQueryVO 並更新 shiftTypes 字段
                shiftSchedules.getSchedulesDates().stream()
                        .filter(schedule -> schedule.getShiftDate().equals(date))
                        .findFirst()
                        .ifPresent(schedule -> {
                            schedule.setShiftTypes(selectedShiftType != null ? selectedShiftType.getShiftKey() : null);
                            TextField remarkField = remarkFieldsMap.get(date);
                            if (remarkField != null) {
                                schedule.setRemark(remarkField.getValue());
                            }
                        });
            }
            // 觸發 UpdateEvent，將更新後的 shiftSchedules 傳遞出去
            fireEvent(new UpdateEvent(this, shiftSchedules));
        }
    }

    private void setBinder() {
        binder.forField(new ComboBox<>()) // 使用一个空的绑定模板来初始化
                .bind(
                        shiftSchedules -> null, // 不实际绑定具体字段
                        (shiftSchedules, selectedOption) -> {
                        } // 只在后续操作中使用 ComboBox 数据
                );
        binder.bindInstanceFields(this); // 绑定实例字段
    }

    public void setShiftSchedules(ShiftSchedulesQueryVO shiftSchedules) {
        binder.setBean(shiftSchedules);
    }

    // Events
    public static abstract class ShiftSchedulesQueryDialog1Event extends ComponentEvent<ShiftSchedulesQueryDialog> {
        private final ShiftSchedulesQueryVO shiftSchedules;

        public ShiftSchedulesQueryDialog1Event(ShiftSchedulesQueryDialog source, ShiftSchedulesQueryVO shiftSchedules) {
            super(source, false);
            this.shiftSchedules = shiftSchedules;
        }

        public ShiftSchedulesQueryVO getShiftSchedules() {
            return shiftSchedules;
        }
    }

    public static class UpdateEvent extends ShiftSchedulesQueryDialog1Event {
        public UpdateEvent(ShiftSchedulesQueryDialog source, ShiftSchedulesQueryVO shiftSchedules) {
            super(source, shiftSchedules);
        }
    }

    public static class CloseEvent extends ShiftSchedulesQueryDialog1Event {
        public CloseEvent(ShiftSchedulesQueryDialog source) {
            super(source, null);
        }
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
