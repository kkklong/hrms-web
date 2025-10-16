package com.hrm.application.views.leave.leaveApply;

import com.vaadin.flow.component.dialog.Dialog;

public class LeaveCreateDialog extends Dialog {
//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LeaveCreateDialog.class);
//
//    // service
//    private final LeaveService service;
//
//    // data
//    private List<ShiftType> shiftTypeList;
//    private Map<String, ShiftType> shiftTypeMap;
//    private List<LeaveSpecialRecord> currentSpecialRecordList;
//    private final Map<Integer, LeaveSpecialRecord> currentSpecialRecordMap = new HashMap<>();
//
//    // layout
//    VerticalLayout dateTimeLayout = new VerticalLayout();
//
//    // 元件
//    ComboBox<LeaveSpecialRecord> leaveTypes = new ComboBox<>("請假類型");
//    Select<ShiftType> shiftType = new Select<>();
//    NumberField availableVal = new NumberField("剩餘/可請時數");
//    NumberField countVal = new NumberField("總請假時數");
//    TextArea reason = new TextArea("請假原因");
//    DateTimePicker startDate = new DateTimePicker("請假開始時間");
//    DateTimePicker endDate = new DateTimePicker("請假結束時間");
//    TextArea remark = new TextArea("備註");
//    RadioButtonGroup<Boolean> startWithRest = new RadioButtonGroup<>();
//    RadioButtonGroup<Boolean> endWithRest = new RadioButtonGroup<>();
//    Span hoursLabel = new Span("時數");
//    NumberField hoursField = new NumberField();
//    private Grid<LeaveRecordDateTimeBO> leaveDatesGrid;
//
//    // 按鈕
//    Button addButton = new Button("新增時間");
//    Button removeButton = new Button("清除");
//    Button save = new Button("送出");
//    Button close = new Button("關閉");
//
//    // file
//    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
//    Upload uploadFile = new Upload(buffer);
//    private ImagePreviewLayout imagePreviewLayout;
//    FileListForm fileForm = new FileListForm();
//    private final Map<String, Button> previewButtons = new HashMap<>();
//    private final Map<String, StreamResource> streamResources = new HashMap<>();
//
//
//    // binder
//    Binder<LeaveRecordBO> binder = new BeanValidationBinder<>(LeaveRecordBO.class);
//
//    /**
//     * 新增：請假提醒字樣
//     */
//    private final Span leaveReminder = new Span("提醒：連續請假可於同一單申請，非連續請假勿在同單。多天申請於一單，撤單時同單所有請假皆會撤除。");
//
//
//    public LeaveCreateDialog(LeaveService service) {
//
//        this.service = service;
//        setData();
//
//        setContent();
//
//        getFooter().add(createButtonsLayout());
//
//        setBinder();
//
//        setListenerAction();
//
//    }
//
//    // 設定畫面----------------------------------------------------------------------------------------------------
//
//    private void setData() {
//        shiftTypeList = service.getShiftTypeList();
//        shiftTypeMap = ToolUtil.transToMap(shiftTypeList, ShiftType::getShiftKey);
//        currentSpecialRecordList = service.queryCurrentEmployeeLeaveSpecialRecords();
//        service.queryCurrentEmployeeLeaveSpecialRecords().forEach(item -> currentSpecialRecordMap.put(item.getId(), item));
//    }
//
//    private void setContent() {
//        addClassName("leave-dialog");
//        dateTimeLayout.add(new H4("申請資訊"), setLeaveDatesGrid(), removeButton);
//        setComponentSize();
//        VerticalLayout fileLayout = new VerticalLayout(new H4("附件"), setUploadFile(), fileForm);
//        imagePreviewLayout = new ImagePreviewLayout();
//        VerticalLayout leaveMessage = new VerticalLayout(createSelectionLayout(), leaveReminder, dateTimeLayout, fileLayout);
//        HorizontalLayout contentHt = new HorizontalLayout(leaveMessage, imagePreviewLayout);
//        contentHt.setFlexGrow(1, leaveMessage);
//        contentHt.setFlexGrow(1, imagePreviewLayout);
//        contentHt.setMaxWidth("100%");
//        add(contentHt);
//    }
//
//    private VerticalLayout createSelectionLayout() {
//
//        // 第一行
//        leaveTypes.setLabel("請假類型");
//        leaveTypes.setItems(currentSpecialRecordList);
//        leaveTypes.setItemLabelGenerator(value -> Optional.ofNullable(value).map(LeaveSpecialRecord::getId).map(currentSpecialRecordMap::get).map(LeaveSpecialRecord::getChineseName).orElse("未設定可用假別") +
//                Optional.ofNullable(value).map(LeaveSpecialRecord::getStartDate).map(date -> String.format("%s%s%s", " [", date.getYear(), "]")).orElse(""));
//        shiftType.setLabel("班別");
//        shiftType.setItems(shiftTypeList);
//        shiftType.setItemLabelGenerator(shiftType -> String.format("%s%s%s%s%s%s", shiftType.getShiftName(), " (", shiftType.getStartTime(), "-", shiftType.getEndTime(), ")"));
//        HorizontalLayout typesHt = new HorizontalLayout();
//        typesHt.add(leaveTypes, shiftType);
//        typesHt.setAlignItems(FlexComponent.Alignment.BASELINE);
//
//        // 第二行
//        HorizontalLayout hoursHt = new HorizontalLayout();
//        availableVal.setValue(0.0);
//        availableVal.setReadOnly(true);
//        countVal.setValue(0.0);
//        countVal.setReadOnly(true);
//        hoursHt.add(availableVal, countVal);
//
//        // 第四行
//        HorizontalLayout TimeSetHt = new HorizontalLayout();
//        startDate.setStep(Duration.ofMinutes(30));
//        endDate.setStep(Duration.ofMinutes(30));
//        TimeSetHt.add(startDate, endDate);
//
//        // 第五行
//        startWithRest.setLabel("起始日含休息時數");
//        startWithRest.setItems(true, false);
//        startWithRest.setItemLabelGenerator(value -> value ? "是" : "否");
//        endWithRest.setLabel("結束日含休息時數");
//        endWithRest.setItems(true, false);
//        endWithRest.setItemLabelGenerator(value -> value ? "是" : "否");
//        HorizontalLayout WithRestHt = new HorizontalLayout();
//        WithRestHt.add(startWithRest, endWithRest);
//
//        // 第六行
//        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        hoursField.setReadOnly(true);
//        HorizontalLayout hoursLayout = new HorizontalLayout(hoursLabel, hoursField);
//        hoursLayout.setAlignItems(FlexComponent.Alignment.CENTER);
//        HorizontalLayout addRequestHoursHt = new HorizontalLayout();
//        addRequestHoursHt.add(addButton, hoursLayout);
//        addRequestHoursHt.setWidth("100%");
//        addRequestHoursHt.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//
//        leaveReminder.addClassName("leave-reminder-text");
//        leaveReminder.setWidthFull();
//
//        // 假單內容
//        return new VerticalLayout(
//                typesHt,
//                hoursHt,
//                reason,
//                TimeSetHt,
//                WithRestHt,
//                addRequestHoursHt
//        );
//    }
//
//    private Grid<LeaveRecordDateTimeBO> setLeaveDatesGrid() {
//        leaveDatesGrid = new Grid<>(LeaveRecordDateTimeBO.class, false);
//        // 自動長高
//        leaveDatesGrid.setAllRowsVisible(true);
//        leaveDatesGrid.addColumn(LeaveRecordDateTimeBO::getStartDate).setHeader("開始日期");
//        leaveDatesGrid.addColumn(LeaveRecordDateTimeBO::getEndDate).setHeader("結束日期");
//        leaveDatesGrid.addColumn(leaveRecordDateTime -> {
//            String shiftKey = leaveRecordDateTime.getShiftType();
//            ShiftType shiftType = shiftTypeMap.get(shiftKey);
//            return shiftType != null ? shiftType.getShiftName() : "Unknown";
//        }).setHeader("班次類型");
//        leaveDatesGrid.addColumn(LeaveRecordDateTimeBO::getCountVal).setHeader("時數");
//        leaveDatesGrid.addColumn(leaveRecordsDateTime ->
//                leaveRecordsDateTime.getIncludesBreak() ? "是" : "否").setHeader("含休息時數");
//        leaveDatesGrid.getColumns().forEach(col -> col.setAutoWidth(true));
//
//        return leaveDatesGrid;
//    }
//
//    public Upload setUploadFile() {
//        uploadFile.setMaxFiles(10 * 1024 * 1024);  //預設是1M
//        uploadFile.setAcceptedFileTypes("image/jpeg", "image/png", "image/jpg", ".pdf", ".txt", ".doc", ".xls", ".docx", ".xlsx", ".png");
//        uploadFile.setDropAllowed(false);
//        uploadFile.setMaxFiles(1);
//        Button uploadButton = new Button("上傳檔案", new Icon(VaadinIcon.UPLOAD));
//        uploadButton.getStyle().set("--vaadin-button-border", "1px solid");
//        uploadFile.setUploadButton(uploadButton);
//
//        uploadFile.addSucceededListener(event -> fileForm.handleFileUpload(event.getFileName(), buffer.getInputStream(event.getFileName()), imagePreviewLayout));
//        uploadFile.getElement().addEventListener("file-remove", domEvent -> {
//            String fileName = domEvent.getEventData().getString("event.detail.file.name");
//            fileForm.handleFileRemove(fileName);
//            imagePreviewLayout.hidePreview();
//            //從 buffer 中移除
//            buffer.getFiles().remove(fileName);
//        }).addEventData("event.detail.file.name");
//
//        return uploadFile;
//    }
//
//    private void setComponentSize() {
//        countVal.getStyle().set("--vaadin-input-field-readonly-border", "1px solid");
//        leaveTypes.setWidth("20em");
//        shiftType.setWidth("20em");
//        startDate.setWidth("20em");
//        endDate.setWidth("20em");
//        hoursField.setWidth("10em");
//        hoursField.getStyle().set("--vaadin-input-field-readonly-border", "1px solid");
//        countVal.setWidth("20em");
//        availableVal.setWidth("20em");
//        reason.setWidth("40em");
//        fileForm.setWidth("40em");
//    }
//
//    private Component createButtonsLayout() {
//        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//        close.addClickShortcut(Key.ESCAPE);
//
//        return new HorizontalLayout(save, close);
//    }
//
//    private void setBinder() {
//        binder.bind(leaveTypes, record -> currentSpecialRecordMap.get(record.getLeaveSpecialRecordsId()), (record, select) -> record.setLeaveSpecialRecordsId(select == null ? null : select.getId()));
//        binder.bindInstanceFields(this);
//    }
//
//    private void setListenerAction() {
//
//        // 選擇假別，更新假別剩餘時數
//        leaveTypes.addValueChangeListener(event -> {
//            LeaveSpecialRecord record = event.getValue();
//            if (record != null) {
//                availableVal.setValue(service.getAvailableHours(record.getId()));
//                availableVal.setHelperText("最低請假單位: " + record.getMinLeaveUnit() + " 小時");
//            } else {
//                // 當 selectedLeaveType 為 null 時的處理邏輯
//                availableVal.clear();
//                availableVal.setHelperText("");
//            }
//        });
//
//        // 班別決定
//        shiftType.addValueChangeListener(event -> enableRestSelection(event.getValue()));
//
//        // 計算時數
//        leaveTypes.addValueChangeListener(event -> updateHoursField());
//        shiftType.addValueChangeListener(event -> updateHoursField());
//        startDate.addValueChangeListener(event -> updateHoursField());
//        endDate.addValueChangeListener(event -> updateHoursField());
//        startWithRest.addValueChangeListener(event -> updateHoursField());
//        endWithRest.addValueChangeListener(event -> updateHoursField());
//
//
//        // 新增&移除時間
//        addButton.addClickListener(event -> {
//            addDateTimes(binder.getBean());
//        });
//        removeButton.addClickListener(event -> {
//            showDateTimeLayout(false);
//            binder.getBean().getLeaveDates().clear();
//            countVal.setValue(0.0);
//        });
//
//        save.addClickListener(event -> validateAndSave());
//        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
//
//        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
//    }
//
//    // 內部操作----------------------------------------------------------------------------------------------------
//
//    /**
//     * 確認班別設置休息補償
//     */
//    private void enableRestSelection(ShiftType type) {
//        if (service.checkRegularBreakTime(type)) {
//            startWithRest.setEnabled(false);
//            endWithRest.setEnabled(false);
//        } else {
//            startWithRest.setEnabled(true);
//            endWithRest.setEnabled(true);
//        }
//    }
//
//    /**
//     * 計算選取的請假時數
//     */
//    private void updateHoursField() {
//        if (shiftType.getValue() == null || leaveTypes.getValue() == null) {
//            return;
//        }
//        if (startDate.getValue() == null || endDate.getValue() == null || startWithRest.getValue() == null || endWithRest.getValue() == null) {
//            hoursField.setValue(0.0);
//            return;
//        }
//        // 驗證時間先後
//        if (!startDate.getValue().isBefore(endDate.getValue())) {
//            hoursField.setValue(0.0);
//            return;
//        }
//
//        // 取得請假時間列表
//        List<LeaveRecordDateTimeBO> dateTimeList = service.analyzeWorkDateTime(shiftType.getValue(), leaveTypes.getValue(),
//                startDate.getValue(), endDate.getValue(), startWithRest.getValue(), endWithRest.getValue());
//
//        // 請假時數
//        double totalHours = dateTimeList.stream().mapToDouble(LeaveRecordDateTimeBO::getCountVal).sum();
//        hoursField.setValue(totalHours);
//    }
//
//    /**
//     * 新增時間資料
//     */
//    private void addDateTimes(LeaveRecordBO leaveRecord) {
//        if (!checkSelectValue()) {
//            return;
//        }
//
//        // 取得請假時間列表
//        List<LeaveRecordDateTimeBO> dateTimeList = service.analyzeWorkDateTime(shiftType.getValue(), leaveTypes.getValue(),
//                startDate.getValue(), endDate.getValue(), startWithRest.getValue(), endWithRest.getValue());
//
//        // 請假時數
//        double totalHours = dateTimeList.stream().mapToDouble(LeaveRecordDateTimeBO::getCountVal).sum();
//        if (!checkHoursField(totalHours)) {
//            return;
//        }
//
//        // 加入總請假列表
//        List<LeaveRecordDateTimeBO> recordDateTimeList = leaveRecord.getLeaveDates();
//        if (recordDateTimeList == null) {
//            recordDateTimeList = new ArrayList<>();
//            leaveRecord.setLeaveDates(recordDateTimeList);
//        }
//        recordDateTimeList.addAll(dateTimeList);
//        displayDateTimes(recordDateTimeList);
//
//        // 總請假時數
//        double recordTotalHours = recordDateTimeList.stream().mapToDouble(LeaveRecordDateTimeBO::getCountVal).sum();
//        countVal.setValue(recordTotalHours);
//    }
//
//    private void displayDateTimes(List<LeaveRecordDateTimeBO> dateTimeList) {
//        if (dateTimeList != null && !dateTimeList.isEmpty()) {
//            leaveDatesGrid.setItems(dateTimeList);
//            // 重新整理表格(高度)
//            leaveDatesGrid.getDataProvider().refreshAll();
//            showDateTimeLayout(true);
//        } else {
//            showDateTimeLayout(false);
//        }
//    }
//
//    private void showDateTimeLayout(boolean show) {
//        dateTimeLayout.setVisible(show);
//        leaveTypes.setReadOnly(show);
//    }
//
//    private void validateAndSave() {
//        if (binder.isValid()) {
//            LeaveRecordBO leaveRecord = binder.getBean();
//            if (checkValidHours()) {
//                fireEvent(new SaveEvent(this, leaveRecord, buffer));
//            }
//        }
//    }
//
//    // 外部操作----------------------------------------------------------------------------------------------------
//
//    /**
//     * 外部綁值
//     *
//     * @param leaveRecord
//     */
//    public void setLeave(LeaveRecordBO leaveRecord) {
//        resetDialog();
//        binder.setBean(leaveRecord);
//    }
//
//    /**
//     * 清除上一張假單的資料
//     */
//    public void resetDialog() {
//        leaveTypes.clear();
//        shiftType.clear();
//        availableVal.clear();
//        countVal.clear();
//        startDate.setValue(null);
//        endDate.setValue(null);
//        startWithRest.setValue(false);
//        endWithRest.setValue(false);
//        hoursField.clear();
//
//        showDateTimeLayout(false);
//
//        fileForm.removeAll();
//        imagePreviewLayout.hidePreview();
//        uploadFile.clearFileList();
//        buffer = new MultiFileMemoryBuffer();  // 清除 buffer
//        uploadFile.setReceiver(buffer);  // 重新設置 buffer 到 uploadFile
//    }
//
//    // 驗證欄位----------------------------------------------------------------------------------------------------
//
//    /**
//     * 欄位空值驗證
//     */
//    private boolean checkSelectValue() {
//        if (leaveTypes.getValue() == null) {
//            leaveTypes.setInvalid(true);
//            leaveTypes.setErrorMessage("需選擇假別");
//            Notification.show("需選擇假別");
//            return false;
//        }
//        if (shiftType.getValue() == null) {
//            shiftType.setInvalid(true);
//            shiftType.setErrorMessage("需選擇班別");
//            Notification.show("需選擇班別");
//            return false;
//        }
//        if (startDate.getValue() == null) {
//            startDate.setInvalid(true);
//            startDate.setErrorMessage("請選擇日期");
//            Notification.show("請選擇日期");
//            return false;
//        }
//        if (endDate.getValue() == null) {
//            endDate.setInvalid(true);
//            endDate.setErrorMessage("請選擇日期");
//            Notification.show("請選擇日期");
//            return false;
//        }
//        if (!startDate.getValue().isBefore(endDate.getValue())) {
//            endDate.setInvalid(true);
//            endDate.setErrorMessage("結束時間不能小於開始時間");
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 時數驗證
//     */
//    private boolean checkHoursField(Double hoursValue) {
//        try {
//            Float minLeaveUnit = leaveTypes.getValue().getMinLeaveUnit();
//            if (hoursValue < minLeaveUnit) {
//                hoursField.setInvalid(true);
//                hoursField.setErrorMessage("時數小於[" + leaveTypes.getValue().getChineseName() + "]最低請假單位");
//                return false;
//            }
//            if (hoursValue % minLeaveUnit != 0) {
//                hoursField.setInvalid(true);
//                hoursField.setErrorMessage("時數不符[" + leaveTypes.getValue().getChineseName() + "]最低請假單位*N");
//                return false;
//            }
//            if (hoursValue > availableVal.getValue()) {
//                hoursField.setInvalid(true);
//                hoursField.setErrorMessage("[" + leaveTypes.getValue().getChineseName() + "]" + "時數不足");
//                return false;
//            }
//        } catch (NumberFormatException e) {
//            hoursField.setInvalid(true);
//            hoursField.setErrorMessage("請輸入一個有效的數字");
//            return false;
//        }
//        return true;
//    }
//
//    public boolean checkValidHours() {
//        if (leaveTypes.getValue() == null) {
//            Notification.show("無可用假別");
//            return false;
//        }
//        if (countVal.getValue() == 0) {
//            Notification.show("時數不足 or 尚未新增申請資訊");
//            return false;
//        }
//        if (countVal.getValue() > availableVal.getValue()) {
//            Notification.show("[" + leaveTypes.getValue().getChineseName() + "]" + "時數不足");
//            return false;
//        }
//        return true;
//    }
//
//
//    // Events
//    public static abstract class LeaveDialogEvent extends ComponentEvent<LeaveCreateDialog> {
//        private final LeaveRecordBO leaveRecord;
//        private final MultiFileMemoryBuffer buffer;
//
//        public LeaveDialogEvent(LeaveCreateDialog source, LeaveRecordBO leaveRecord, MultiFileMemoryBuffer buffer) {
//            super(source, false);
//            this.leaveRecord = leaveRecord;
//            this.buffer = buffer;
//        }
//
//        public LeaveRecordBO getLeaveRecords() {
//            return leaveRecord;
//        }
//
//        public MultiFileMemoryBuffer getBuffer() {
//            return buffer;
//        }
//    }
//
//    public static class SaveEvent extends LeaveDialogEvent {
//        SaveEvent(LeaveCreateDialog source, LeaveRecordBO leaveRecord, MultiFileMemoryBuffer buffer) {
//            super(source, leaveRecord, buffer);
//        }
//    }
//
//    public static class CloseEvent extends LeaveDialogEvent {
//        CloseEvent(LeaveCreateDialog source) {
//            super(source, null, null);
//        }
//    }
//
//    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
//        return addListener(SaveEvent.class, listener);
//    }
//
//    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
//        return addListener(CloseEvent.class, listener);
//    }

}
