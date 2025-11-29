package com.hrm.application.component;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 共用的年月切換元件：
 * - 左右按鈕切換月份
 * - 中間 ComboBox 顯示 YYYY-MM
 */
public class MonthNavigator extends Composite<FlexLayout> {

    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final Button prevButton  = new Button("<");
    private final Button nextButton  = new Button(">");
    private final ComboBox<YearMonth> monthPicker = new ComboBox<>();
    private final List<YearMonth> months;

    /**
     * initialDate：初始月份
     * previousYears / nextYears：可選範圍 = initialDate 的前年 / 後年數
     */
    public MonthNavigator(LocalDate initialDate, int previousYears, int nextYears) {
        LocalDate init = (initialDate != null) ? initialDate : LocalDate.now();
        YearMonth center = YearMonth.from(init);

        int backYears  = Math.max(previousYears, 0);
        int nextYears0 = Math.max(nextYears, 0);

        int backMonths    = backYears * 12;
        int forwardMonths = nextYears0 * 12;
        int totalRange    = backMonths + forwardMonths;

        YearMonth start = center.minusMonths(backMonths);
        this.months = IntStream.rangeClosed(0, totalRange)
                .mapToObj(start::plusMonths)
                .collect(Collectors.toList());

        var layout = getContent();
        layout.setAlignItems(Alignment.CENTER);

        monthPicker.setItems(months);
        monthPicker.setItemLabelGenerator(ym -> ym.format(YM_FMT));
        monthPicker.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        monthPicker.addThemeVariants(ComboBoxVariant.LUMO_ALIGN_CENTER);
//        monthPicker.getStyle().set("text-align", "center");

        YearMonth initValue = months.contains(center) ? center : months.get(backMonths);
        monthPicker.setValue(initValue);

        prevButton.getStyle().set("min-inline-size", "4rem");
        nextButton.getStyle().set("min-inline-size", "4rem");

        layout.add(prevButton, monthPicker, nextButton);
        layout.getStyle().set("gap", "0 0.75rem"); // 元件間距（左右留距）
        layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);

        prevButton.addClickListener(e -> shift(-1));
        nextButton.addClickListener(e -> shift(+1));

        monthPicker.addValueChangeListener(e -> {
            YearMonth oldVal = e.getOldValue();
            YearMonth newVal = e.getValue();
            if (newVal == null || Objects.equals(oldVal, newVal)) {
                return;
            }
            updateButtonState();
            fireEvent(new MonthChangeEvent(this, e.isFromClient(), newVal));
        });

        updateButtonState();
    }

    public MonthNavigator(LocalDate initialDate) {
        this(initialDate, 1, 1);
    }

    private void shift(int delta) {
        YearMonth current = monthPicker.getValue();
        int idx = months.indexOf(current);
        if (idx < 0) {
            return;
        }
        int newIdx = idx + delta;
        if (newIdx < 0 || newIdx >= months.size()) {
            return;
        }
        monthPicker.setValue(months.get(newIdx));
    }

    /** 根據目前 index 決定左右按鈕 enable 狀態 */
    private void updateButtonState() {
        YearMonth current = monthPicker.getValue();
        int idx = months.indexOf(current);

        boolean hasPrev = idx > 0;
        boolean hasNext = idx >= 0 && idx < months.size() - 1;

        prevButton.setEnabled(hasPrev);
        nextButton.setEnabled(hasNext);
    }

    /** 目前選到的月份的 1 號 */
    public LocalDate getSelectedDate() {
        return monthPicker.getValue().atDay(1);
    }

    public static class MonthChangeEvent extends ComponentEvent<MonthNavigator> {
        private final YearMonth value;

        public MonthChangeEvent(MonthNavigator source, boolean fromClient, YearMonth value) {
            super(source, fromClient);
            this.value = value;
        }

        public YearMonth getSelectedMonth() {
            return value;
        }

        public LocalDate getSelectedDate() {
            return value.atDay(1);
        }
    }

    public Registration addValueChangeListener(ComponentEventListener<MonthChangeEvent> listener) {
        return addListener(MonthChangeEvent.class, listener);
    }
}
