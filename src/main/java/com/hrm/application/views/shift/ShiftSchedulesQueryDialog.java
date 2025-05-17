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
        VerticalLayout Vt = new VerticalLayout();
        TextField countDetail = new TextField();
        countDetail.setReadOnly(true);
        countDetail.setWidth("20em");
        countDetail.getStyle().set("font-weight", "bold");
        countDetail.getStyle().set("--vaadin-input-field-readonly-border", "none");
        Vt.add(new HorizontalLayout(new H3("員工: " + shiftSchedules.getNickName() + " 排班"), countDetail), new Hr());
        Vt.addClassName("background-plan");
        Vt.setMinWidth("40em");

        Vt.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        FormLayout contentLayout = new FormLayout();
        contentLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 7));
        contentLayout.setMaxWidth("100em");
        contentLayout.setMinWidth("40em");
        List<ShiftSchedulesDateTimeQueryVO> datesList = shiftSchedules.getSchedulesDates();

        // 添加星期一到星期日的標題
        DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.CHINESE);
        for (int i = 0; i < 7; i++) {
            LocalDate anyDate = LocalDate.now().with(DayOfWeek.of(i + 1)); // 使用任意日期來獲取星期幾名稱
            Div dayOfWeekLabel = new Div(anyDate.format(dayOfWeekFormatter));
            dayOfWeekLabel.getStyle().set("font-weight", "bold");
            dayOfWeekLabel.getStyle().set("text-align", "center");
            contentLayout.add(dayOfWeekLabel);
        }

        int itemCount = 0; // 計數器
        for (int i = 0; i < datesList.size(); i++) {
            LocalDate date = datesList.get(i).getShiftDate();
            // 計算每週的開始位置：在周一前加入空白格
            if (i == 0) {  // 對於第一個日期
                int emptySlots = date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue(); // 得到應加的空白數量
                for (int j = 0; j < emptySlots; j++) {
                    contentLayout.add(new TextField()); // 添加空白格
                    itemCount++;
                }
            }
            // 找到該日期對應的班次安排
            ShiftSchedulesDateTimeQueryVO shiftSchedulesDateTime = shiftSchedules.getSchedulesDates().stream()
                    .filter(schedule -> schedule.getShiftDate().equals(date))
                    .findFirst()
                    .orElse(new ShiftSchedulesDateTimeQueryVO()); // 如果找不到，創建一默認值

            ComboBox<ShiftType> shiftTypesField = new ComboBox<>(date.format(dateFormatter));
            shiftTypesField.setWidthFull();
            shiftTypesField.setMinWidth("0%");
            shiftTypesField.getStyle().set("--vaadin-input-field-border-width", "1.5px");
            shiftTypesField.getElement().getStyle().set("font-size", "14px");
            shiftTypesField.getStyle().set("--vaadin-combo-box-overlay-width", "11em");
            shiftTypesField.setItems(shiftTypeList);
            shiftTypesField.setLabel(shiftSchedulesDateTime.getActionType() == 0
                    ? date.format(dateFormatter) : date.format(dateFormatter) + " [R]");
            shiftTypesField.setReadOnly(shiftSchedulesDateTime.getActionType() == 1);
            shiftTypesField.getStyle().set("--vaadin-input-field-readonly-border", "1px solid");
            shiftTypesField.addValueChangeListener(e -> {
                if (e.getValue() != null && e.getValue().getShiftKey().contains("HOLIDAY")) {
                    shiftTypesField.removeClassName("shiftType-combo-shiftday");
                    shiftTypesField.addClassName("shiftType-combo-holiday");
                } else {
                    shiftTypesField.removeClassName("shiftType-combo-holiday");
                    shiftTypesField.addClassName("shiftType-combo-shiftday");
                }
                setBackgroundColor(shiftTypesField, shiftTypesField.getValue().getShiftColorCode());
                countDetail.setValue(String.format("[休假日: %d] [例假日: %d] [國定假: %d]"
                        , getShiftTypeCount(comboBoxes, "REST")
                        , getShiftTypeCount(comboBoxes, "REGULAR")
                        , getShiftTypeCount(comboBoxes, "NATIONAL")
                ));
            });
            // 设置当前值
            shiftTypesField.setValue(Optional.ofNullable(shiftTypeMap.get(shiftSchedulesDateTime.getShiftTypes())).orElse(null));
            shiftTypesField.setItemLabelGenerator(ShiftType::getShiftName);
            // 保存 ComboBox 以便在保存时读取用户的选择
            comboBoxes.put(date, shiftTypesField);
            countDetail.setValue(String.format("[休假日: %d] [例假日: %d] [國定假: %d]"
                    , getShiftTypeCount(comboBoxes, "REST")
                    , getShiftTypeCount(comboBoxes, "REGULAR")
                    , getShiftTypeCount(comboBoxes, "NATIONAL")
            ));
            contentLayout.add(shiftTypesField);

            itemCount++; // 每次添加一筆資料，計數器加 1
            if (itemCount % 14 == 0) {
                Vt.add(contentLayout, new Hr());
                contentLayout = new FormLayout(); // 創建新的 FormLayout
                contentLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 7));
                contentLayout.setMaxWidth("100em");
                contentLayout.setMinWidth("40em");
            }
        }
        // 將最後剩餘的 contentLayout 添加到 Vt
        if (itemCount % 14 != 0) {
            Vt.add(contentLayout);
        }
        return Vt;
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
                        .ifPresent(schedule -> schedule.setShiftTypes(selectedShiftType != null ? selectedShiftType.getShiftKey() : null));
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
    public static abstract class ShiftSchedulesQueryDialogEvent extends ComponentEvent<ShiftSchedulesQueryDialog> {
        private final ShiftSchedulesQueryVO shiftSchedules;

        public ShiftSchedulesQueryDialogEvent(ShiftSchedulesQueryDialog source, ShiftSchedulesQueryVO shiftSchedules) {
            super(source, false);
            this.shiftSchedules = shiftSchedules;
        }

        public ShiftSchedulesQueryVO getShiftSchedules() {
            return shiftSchedules;
        }
    }

    public static class UpdateEvent extends ShiftSchedulesQueryDialogEvent {
        public UpdateEvent(ShiftSchedulesQueryDialog source, ShiftSchedulesQueryVO shiftSchedules) {
            super(source, shiftSchedules);
        }
    }

    public static class CloseEvent extends ShiftSchedulesQueryDialogEvent {
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
