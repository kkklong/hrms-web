package com.hrm.application.views.notice;

import com.hrm.application.entity.Department;
import com.hrm.application.entity.Notice;
import com.hrm.application.model.Option;
import com.hrm.application.service.NoticeService;
import com.hrm.application.util.NotificationUtil;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static com.hrm.application.util.ToolUtil.dataConverter;

public class NoticeDialog extends Dialog {

    private NoticeService service;

    private ComboBox<Option<String>> type = new ComboBox<>("分類");
    private TextField title = new TextField("標題");
    private TextArea content = new TextArea("內文");
    private DateTimePicker publishDate = new DateTimePicker("發布時間");
    private DateTimePicker endDate = new DateTimePicker("撤銷時間");
    private Button save = new Button("儲存");
    private Button update = new Button("更新");
    private Button saveAndSend = new Button("儲存並發送");
    private Button delete = new Button("刪除");
    private Button close = new Button("取消");
    List<Option<String>> noticeTypeOptionList;

//    FileListForm fileForm = new FileListForm();
    // file
    private MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    private Upload uploadFile = new Upload(buffer);

//    private ImagePreviewLayout imagePreviewLayout; // 右側預覽區域
    private Binder<Notice> binder = new BeanValidationBinder<>(Notice.class);

    public NoticeDialog(List<Option<String>> noticeTypeOptionList) {
        this.noticeTypeOptionList = noticeTypeOptionList;
        setData();
        getContent();
        getFooter().add(createButtonsLayout());
    }

    private void setComponentSize() {
        type.setWidth("20em");
        title.setWidth("20em");
        content.setWidth("40em");
        content.setHeight("20em");
        publishDate.setWidth("20em");
        endDate.setWidth("20em");
//        fileForm.setWidth("40em");
    }

    private void getContent() {
        setComponentSize();
        VerticalLayout noticeMessage = new VerticalLayout(configureForm());
        add(noticeMessage);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveAndSend.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        update.addClickListener(event -> validateAndUpdate());
        saveAndSend.addClickListener(event -> validateAndSaveAndSend());
        delete.addClickListener(event -> fireEvent(new NoticeDialog.DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new NoticeDialog.CloseEvent(this)));
        return new HorizontalLayout(save, update, saveAndSend, delete, close);
    }

    private void setData() {
        type.setItems(noticeTypeOptionList);
        type.setItemLabelGenerator(Option::getName);
        binder.forField(type)
                .withConverter(dataConverter(noticeTypeOptionList))
                .bind(Notice::getType, Notice::setType);
        binder.bindInstanceFields(this);
    }

    private VerticalLayout configureForm() {
        publishDate.setStep(Duration.ofMinutes(30));
        endDate.setStep(Duration.ofMinutes(30));
        return new VerticalLayout(
                new HorizontalLayout(title, type),
                new HorizontalLayout(publishDate, endDate),
                content
        );
    }

    public void setDialogView (Boolean isCreate) {
        save.setVisible(isCreate);
        update.setVisible(!isCreate);
        update.setVisible(!isCreate);
        delete.setVisible(!isCreate);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new NoticeDialog.SaveEvent(this, binder.getBean(), buffer));
        }
    }

    private void validateAndUpdate() {
        if (binder.isValid()) {
            fireEvent(new NoticeDialog.UpdateEvent(this, binder.getBean(), buffer));
        }
    }

    private void validateAndSaveAndSend() {
        if (binder.isValid()) {
            fireEvent(new NoticeDialog.SaveAndSendEvent(this, binder.getBean(), buffer));
        }
    }

    public void setNotice(Notice notice) {
        resetDialog();
        binder.setBean(notice);
//        fileForm.displayFiles(notice.getFiles(), imagePreviewLayout);
    }

    private void resetDialog() {
//        fileForm.removeAll();
//        imagePreviewLayout.hidePreview();
        uploadFile.clearFileList();
        buffer = new MultiFileMemoryBuffer();  // 清除 buffer
        uploadFile.setReceiver(buffer);  // 重新設置 buffer 到 uploadFile
    }

    public static abstract class NoticeDialogEvent extends ComponentEvent<NoticeDialog> {
        private final Notice notice;
        private final MultiFileMemoryBuffer buffer;

        protected NoticeDialogEvent(NoticeDialog source, Notice notice, MultiFileMemoryBuffer buffer) {
            super(source, false);
            this.notice = notice;
            this.buffer = buffer;
        }

        public Notice getNotice() {
            return notice;
        }

        public MultiFileMemoryBuffer getBuffer() {
            return buffer;
        }
    }

    public static class SaveEvent extends NoticeDialogEvent {
        SaveEvent(NoticeDialog source, Notice notice, MultiFileMemoryBuffer buffer) {
            super(source, notice, buffer);
        }
    }

    public static class UpdateEvent extends NoticeDialogEvent {
        UpdateEvent(NoticeDialog source, Notice notice, MultiFileMemoryBuffer buffer) {
            super(source, notice, buffer);
        }
    }

    public static class SaveAndSendEvent extends NoticeDialogEvent {
        SaveAndSendEvent(NoticeDialog source, Notice notice, MultiFileMemoryBuffer buffer) {
            super(source, notice, buffer);
        }
    }

    public static class DeleteEvent extends NoticeDialogEvent {
        DeleteEvent(NoticeDialog source, Notice notice) {
            super(source, notice, null);
        }
    }

    public static class CloseEvent extends NoticeDialogEvent {
        CloseEvent(NoticeDialog source) {
            super(source, null, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(NoticeDialog.SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(NoticeDialog.UpdateEvent.class, listener);
    }

    public Registration addSaveAndSendListener(ComponentEventListener<NoticeDialog.SaveAndSendEvent> listener) {
        return addListener(NoticeDialog.SaveAndSendEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<NoticeDialog.DeleteEvent> listener) {
        return addListener(NoticeDialog.DeleteEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<NoticeDialog.CloseEvent> listener) {
        return addListener(NoticeDialog.CloseEvent.class, listener);
    }
//    @Override
//    protected void onAttach(AttachEvent attachEvent) {
//        super.onAttach(attachEvent);
//        UI ui = attachEvent.getUI();
//        ui.access(() -> {
//            try {
//                setData();
//            } catch (Exception e) {
//                NotificationUtil.error("載入資料失敗：" + e.getMessage());
//            }
//        });
//    }
}
