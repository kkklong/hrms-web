package com.hrm.application.views.notice;

import com.hrm.application.component.FileListForm;
import com.hrm.application.component.ImagePreviewLayout;
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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import java.util.Arrays;
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

    // ---- file ----
    FileListForm fileForm = new FileListForm();

    private MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    private Upload uploadFile = new Upload(buffer);
    private ImagePreviewLayout imagePreviewLayout; // 右側預覽區域
    private Binder<Notice> binder = new BeanValidationBinder<>(Notice.class);

    public NoticeDialog(List<Option<String>> noticeTypeOptionList) {
        this.noticeTypeOptionList = noticeTypeOptionList;
        setData();
        getContent();
        getFooter().add(createButtonsLayout());
    }

    public Upload setUploadFile() {
        uploadFile.setMaxFiles(10 * 1024 * 1024);  //預設是1M
        uploadFile.setAcceptedFileTypes("image/jpeg", "image/png", "image/jpg", ".pdf", ".txt", ".doc", ".xls", ".docx", ".xlsx", ".png");
        uploadFile.setDropAllowed(false);
        uploadFile.setMaxFiles(1);
        Button uploadButton = new Button("上傳檔案", new Icon(VaadinIcon.UPLOAD));
        uploadButton.getStyle().set("--vaadin-button-border", "1px solid");
        uploadFile.setUploadButton(uploadButton);

        uploadFile.addSucceededListener(event -> fileForm.handleFileUpload(event.getFileName(), buffer.getInputStream(event.getFileName()), imagePreviewLayout));
        uploadFile.getElement().addEventListener("file-remove", domEvent -> {
            String fileName = domEvent.getEventData().getString("event.detail.file.name");
            fileForm.handleFileRemove(fileName);
            imagePreviewLayout.hidePreview();
            //從 buffer 中移除
            buffer.getFiles().remove(fileName);
        }).addEventData("event.detail.file.name");

        return uploadFile;
    }

    private void setComponentSize() {
//        type.setWidth("20em");
//        title.setWidth("20em");
//        content.setWidthFull();
        content.setMinHeight("40em");
//        publishDate.setWidth("20em");
//        endDate.setWidth("20em");
//        fileForm.setWidth("40em");
        List<HasSize> fields = Arrays.asList(
                type, title, content, publishDate, endDate, fileForm
        );
        fields.forEach(field -> {
            field.setWidthFull();
        });
    }

    private void getContent() {
        VerticalLayout fileLayout = new VerticalLayout(new H4("附件"), setUploadFile(), fileForm);
        setComponentSize();
        imagePreviewLayout = new ImagePreviewLayout();
        VerticalLayout noticeMessage = new VerticalLayout(configureForm(), fileLayout);
        HorizontalLayout contentHt = new HorizontalLayout(noticeMessage, imagePreviewLayout);
        contentHt.setFlexGrow(1, noticeMessage);
        contentHt.setFlexGrow(1, imagePreviewLayout);
        contentHt.setMaxWidth("100%");
        add(contentHt);
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
        FormLayout form = new FormLayout();
        form.add(title, type, publishDate, endDate);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("30em", 2)
        );
        form.setWidth("50em");
        return new VerticalLayout(
                form,
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
        fileForm.displayFiles(notice.getFiles(), imagePreviewLayout);
    }

    private void resetDialog() {
        fileForm.removeAll();
        imagePreviewLayout.hidePreview();
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

}
