package com.hrm.application.views.leave.leaveApply;

import com.vaadin.flow.component.dialog.Dialog;

public class LeaveDialog extends Dialog {
//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LeaveDialog.class);
//
//    // service
//    private final LeaveService service;
//
//    private Map<String, ShiftType> shiftTypeMap;
//    private List<Option<String>> leaveTypeList;
//    private final Map<String, Option<String>> leaveTypeMap = new HashMap<>();
//
//    // layout
//    VerticalLayout dateTimeLayout = new VerticalLayout();
//    FileListForm fileForm = new FileListForm();
//
//    // 元件
//    ComboBox<Option<String>> leaveTypes = new ComboBox<>("請假類型");
//    NumberField countVal = new NumberField("總請假時數");
//    TextArea reason = new TextArea("請假原因");
//    RadioButtonGroup<Boolean> attachmentRequired = new RadioButtonGroup<>();
//    TextArea remark = new TextArea("備註");
//    private Grid<LeaveRecordDateTime> leaveDatesGrid;
//
//    // 按鈕
//    Button update = new Button("補件");
//    Button cancel = new Button("撤銷");
//    Button close = new Button("關閉");
//
//    // file
//    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
//    Upload uploadFile = new Upload(buffer);
//    private ImagePreviewLayout imagePreviewLayout; // 右側預覽區域
//
//    // binder
//    Binder<LeaveRecord> binder = new BeanValidationBinder<>(LeaveRecord.class);
//
//
//    public LeaveDialog(LeaveService service) {
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
//        shiftTypeMap = ToolUtil.transToMap(service.getShiftTypeList(), ShiftType::getShiftKey);
//        leaveTypeList = service.getLeaveTypeOptionList();
//        leaveTypeList.forEach(item -> leaveTypeMap.put(item.getValue(), item));
//    }
//
//    private void setContent() {
//        addClassName("leave-dialog");
//        dateTimeLayout.add(new H4("申請資訊"), setLeaveDatesGrid());
//        setComponentSize();
//        VerticalLayout fileLayout = new VerticalLayout(new H4("附件"), setUploadFile(), fileForm);
//        imagePreviewLayout = new ImagePreviewLayout();
//        VerticalLayout leaveMessage = new VerticalLayout(createSelectionLayout(), dateTimeLayout, fileLayout);
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
//        leaveTypes.setItems(leaveTypeList);
//        leaveTypes.setItemLabelGenerator(Option::getName);
//        leaveTypes.setReadOnly(true);
//        countVal.setValue(0.0);
//        countVal.setReadOnly(true);
//        HorizontalLayout TypesHt = new HorizontalLayout();
//        TypesHt.add(leaveTypes, countVal);
//        TypesHt.setAlignItems(FlexComponent.Alignment.BASELINE);
//
//        // 第二行
//        reason.setReadOnly(true);
//
//        // 第三行
//        attachmentRequired.setLabel("需要補件");
//        attachmentRequired.setItems(true, false);
//        attachmentRequired.setItemLabelGenerator(value -> value ? "是" : "否");
//        attachmentRequired.setReadOnly(true);
//        attachmentRequired.setVisible(true);
//
//        // 第四行
//        remark.setReadOnly(true);
//        remark.setVisible(true);
//
//        return new VerticalLayout(
//                TypesHt,
//                reason,
//                attachmentRequired,
//                remark
//        );
//    }
//
//    private Grid<LeaveRecordDateTime> setLeaveDatesGrid() {
//        leaveDatesGrid = new Grid<>(LeaveRecordDateTime.class, false);
//        // 自動長高
//        leaveDatesGrid.setAllRowsVisible(true);
//        leaveDatesGrid.addColumn(LeaveRecordDateTime::getStartDate).setHeader("開始日期");
//        leaveDatesGrid.addColumn(LeaveRecordDateTime::getEndDate).setHeader("結束日期");
//        leaveDatesGrid.addColumn(leaveRecordDateTime -> {
//            String shiftKey = leaveRecordDateTime.getShiftType();
//            ShiftType shiftType = shiftTypeMap.get(shiftKey);
//            return shiftType != null ? shiftType.getShiftName() : "Unknown";
//        }).setHeader("班次類型");
//        leaveDatesGrid.addColumn(LeaveRecordDateTime::getCountVal).setHeader("時數");
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
//        leaveTypes.setWidth("20em");
//        countVal.getStyle().set("--vaadin-input-field-readonly-border", "1px solid");
//        countVal.setWidth("20em");
//        reason.setWidth("40em");
//        remark.setWidth("40em");
//        fileForm.setWidth("40em");
//    }
//
//    private Component createButtonsLayout() {
//        cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
//        update.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
//        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//        close.addClickShortcut(Key.ESCAPE);
//
//        return new HorizontalLayout(update, cancel, close);
//    }
//
//    private void setBinder() {
//        binder.bind(leaveTypes, records -> leaveTypeMap.get(records.getLeaveTypes()), (records, select) -> records.setLeaveTypes(select.getValue()));
//        binder.bindInstanceFields(this);
//    }
//
//    private void setListenerAction() {
//        update.addClickListener(event -> validateAndUpdate());
//        cancel.addClickListener(event -> validateAndCancel());
//        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
//    }
//
//    // 內部操作----------------------------------------------------------------------------------------------------
//
//    private void validateAndUpdate() {
//        if (binder.isValid()) {
//            if (buffer.getFiles().isEmpty()) {
//                Notification.show("無資料上傳");
//                return;
//            }
//            fireEvent(new UpdateEvent(this, binder.getBean(), buffer));
//        }
//    }
//
//    private void validateAndCancel() {
//        if (binder.isValid()) {
//            fireEvent(new CancelEvent(this, binder.getBean()));
//        }
//    }
//
//    // 外部操作----------------------------------------------------------------------------------------------------
//
//    public void setLeave(LeaveRecord leaveRecord) {
//        resetDialog();
//        binder.setBean(leaveRecord);
//        displayDateTimes(leaveRecord.getLeaveDates());
//        fileForm.displayFiles(leaveRecord.getFiles(), imagePreviewLayout);
//    }
//
//    private void resetDialog() {
//        fileForm.removeAll();
//        imagePreviewLayout.hidePreview();
//        uploadFile.clearFileList();
//        buffer = new MultiFileMemoryBuffer();  // 清除 buffer
//        uploadFile.setReceiver(buffer);  // 重新設置 buffer 到 uploadFile
//    }
//
//    private void displayDateTimes(List<LeaveRecordDateTime> dateTimeList) {
//        if (dateTimeList != null && !dateTimeList.isEmpty()) {
//            leaveDatesGrid.setItems(dateTimeList);
//            // 重新整理表格(高度)
//            leaveDatesGrid.getDataProvider().refreshAll();
//        }
//    }
//
//    public void setDialogView (Byte staus) {
//        boolean showUpload = staus != 2 && staus != 3 ? true : false; //2：拒絕；3:已銷假
//        uploadFile.setVisible(showUpload);
//        update.setVisible(showUpload);
//        cancel.setVisible(showUpload);
//    }
//
//    // Events
//    public static abstract class LeaveDialogEvent extends ComponentEvent<LeaveDialog> {
//        private final LeaveRecord leaveRecord;
//        private final MultiFileMemoryBuffer buffer;
//
//        public LeaveDialogEvent(LeaveDialog source, LeaveRecord leaveRecord, MultiFileMemoryBuffer buffer) {
//            super(source, false);
//            this.leaveRecord = leaveRecord;
//            this.buffer = buffer;
//        }
//
//        public LeaveRecord getLeaveRecords() {
//            return leaveRecord;
//        }
//
//        public MultiFileMemoryBuffer getBuffer() {
//            return buffer;
//        }
//    }
//
//    public static class UpdateEvent extends LeaveDialogEvent {
//        UpdateEvent(LeaveDialog source, LeaveRecord leaveRecord, MultiFileMemoryBuffer buffer) {
//            super(source, leaveRecord, buffer);
//        }
//    }
//
//    public static class CancelEvent extends LeaveDialogEvent {
//        CancelEvent(LeaveDialog source, LeaveRecord leaveRecord) {
//            super(source, leaveRecord, null);
//        }
//    }
//
//    public static class CloseEvent extends LeaveDialogEvent {
//        CloseEvent(LeaveDialog source) {
//            super(source, null, null);
//        }
//    }
//
//    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
//        return addListener(UpdateEvent.class, listener);
//    }
//
//    public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
//        return addListener(CancelEvent.class, listener);
//    }
//
//    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
//        return addListener(CloseEvent.class, listener);
//    }

}
