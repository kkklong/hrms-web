package com.hrm.application.views.shift;

import com.hrm.application.model.Option;
import com.hrm.application.service.ShiftScheduleService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class ExportShiftSchedulesDialog extends Dialog {

    private final ShiftScheduleService service;
    private final List<Option<Integer>> departmentList;

    private final Button exportButton = new Button("導出");
    private final Button closeButton = new Button("關閉");

    // 下拉選單
    private final ComboBox<Integer> yearComboBox = new ComboBox<>("年份");
    private final ComboBox<Integer> monthComboBox = new ComboBox<>("月份");
    private final ComboBox<Option<Integer>> department = new ComboBox<>("部門");

    public ExportShiftSchedulesDialog(ShiftScheduleService service, Integer year, Integer month, Option<Integer> department) {
        this.service = service;
        this.departmentList = service.getDepartmentOptionList();
        this.setMaxWidth("90%");

        configureDepartment();
        configureYearMonthComboBox();
        configureDialogLayout();
        configureButtons();
        yearComboBox.setValue(year);
        monthComboBox.setValue(month);
        if (department != null) {
            this.department.setValue(department);
        }
        addDialogCloseActionListener(e -> clearComboBoxValues());
    }

    private void configureDepartment() {
        department.setItems(departmentList);
        department.setItemLabelGenerator(Option::getName);
        department.setPlaceholder("選擇部門");
        department.addValueChangeListener(e -> checkInputs());
        department.setMaxWidth("8em");
        department.setMinWidth("3em");
    }

    // 初始化年份和月份combobox
    private void configureYearMonthComboBox() {
        // 年份選擇：從前一年到後一年
        List<Integer> years = IntStream.range(LocalDate.now().getYear() - 1, LocalDate.now().getYear() + 2)
                .boxed()
                .collect(Collectors.toList());
        yearComboBox.setItems(years);
        yearComboBox.setPlaceholder("選擇年份");
        yearComboBox.addValueChangeListener(e -> checkInputs());
        yearComboBox.setMaxWidth("8em");
        yearComboBox.setMinWidth("3em");

        // 月份選擇：1月到12月
        List<Integer> months = IntStream.rangeClosed(1, 12)
                .boxed()
                .collect(Collectors.toList());
        monthComboBox.setItems(months);
        monthComboBox.setPlaceholder("選擇月份");
        monthComboBox.setItemLabelGenerator(month -> month + "月");
        monthComboBox.addValueChangeListener(e -> checkInputs());
        monthComboBox.setMaxWidth("8em");
        monthComboBox.setMinWidth("3em");
    }

    private void configureDialogLayout() {
        H3 title = new H3("導出班表");

        HorizontalLayout inputLayout = new HorizontalLayout(yearComboBox, monthComboBox, department);
//        inputLayout.setMaxWidth("27em");
        VerticalLayout layout = new VerticalLayout(title, inputLayout);
//        layout.setMaxWidth("27em");
        add(inputLayout);
    }

    // 配置按鈕
    private void configureButtons() {
        exportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportButton.setEnabled(false);
        exportButton.addClickListener(e -> exportShiftSchedules());

        closeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        closeButton.addClickListener(e -> {
            clearComboBoxValues();
            close();
        });

        HorizontalLayout footerLayout = new HorizontalLayout(exportButton, closeButton);
        footerLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);

        getFooter().add(footerLayout);
    }

    // 檢查所有輸入是否已填寫
    private void checkInputs() {
        boolean isYearSelected = yearComboBox.getValue() != null;
        boolean isMonthSelected = monthComboBox.getValue() != null;
        boolean isDepartmentSelected = department.getValue() != null;

        exportButton.setEnabled(isYearSelected && isMonthSelected && isDepartmentSelected);
    }

    private void clearComboBoxValues() {
        yearComboBox.clear();
        monthComboBox.clear();
        department.clear();
        close();
    }

    private void exportShiftSchedules() {
        String year = String.valueOf(yearComboBox.getValue());
        String month = String.format("%02d", monthComboBox.getValue());
        Option<Integer> selectedDepartment = department.getValue();

        try {
            String formattedDate = year + "-" + month;
            StreamResource resource = service.exportShiftSchedules(formattedDate, selectedDepartment.getValue(), year + "-" + month);

            // 在創建新的downloadLink之前，清理已有的Anchor
            getChildren()
                    .filter(component -> component instanceof Anchor)
                    .forEach(this::remove);

            // 創建 Anchor 並觸發下載
            Anchor downloadLink = new Anchor(resource, "下載班表");
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.getElement().getStyle().set("display", "none");

            // 將 Anchor 新增到佈局中觸發點擊
            add(downloadLink);
            downloadLink.getElement().callJsFunction("click");
            Notification.show("下載中", 2000, Notification.Position.BOTTOM_CENTER);

        } catch (Exception e) {
            Notification.show("導出過程中發生錯誤：" + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }
}