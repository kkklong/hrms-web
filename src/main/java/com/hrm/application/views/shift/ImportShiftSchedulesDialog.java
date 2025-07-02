package com.hrm.application.views.shift;


import com.hrm.application.service.ShiftScheduleService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImportShiftSchedulesDialog extends Dialog {
    private final Button uploadButton = new Button("上傳");
    private final Button closeButton = new Button("關閉");
    private final ShiftScheduleService service;
    private final MemoryBuffer buffer = new MemoryBuffer(); // 接收上傳檔案
    private final Upload upload = new Upload(buffer); // 檔案上傳組件
    private final Grid<List<String>> filePreviewGrid = new Grid<>(); //預覽grid

    public ImportShiftSchedulesDialog(ShiftScheduleService service) {
        this.service = service;

        addClassName("import-shift-schedules");
        setWidth("600px");
        H3 title = new H3("載入預設班表");

        // 檔案上傳配置
        upload.setAcceptedFileTypes("text/csv");
        upload.setMaxFiles(1);

        //預覽grid列設置
        filePreviewGrid.addColumn(row -> row.get(0)).setHeader("西元日期").setAutoWidth(true);
        filePreviewGrid.addColumn(row -> row.get(1)).setHeader("星期").setAutoWidth(true);
        filePreviewGrid.addColumn(row -> row.get(2)).setHeader("是否放假").setAutoWidth(true);
        filePreviewGrid.addColumn(row -> row.size() > 3 ? row.get(3) : "").setHeader("備註").setAutoWidth(true);

        //grid設置
        filePreviewGrid.setWidthFull();
        filePreviewGrid.setHeight("200px");
        filePreviewGrid.setVisible(false);

        //佈局
        HorizontalLayout uploadLayout = new HorizontalLayout(upload);
        uploadLayout.setWidthFull();
        uploadLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout layout = new VerticalLayout(title, uploadLayout, filePreviewGrid);

        add(layout);
        getFooter().add(createButtonsLayout());

        setEventListener();
    }

    private void setEventListener() {
        //檔案上傳成功後預覽事件
        upload.addSucceededListener(event -> {
            try {
                previewFile();
                uploadButton.setEnabled(true);
            } catch (IOException e) {
                Notification.show("加載失敗",3000, Notification.Position.MIDDLE);
            }
        });

        // 取消上傳後清空預覽區塊
        upload.getElement().addEventListener("file-remove", e -> {
            clearPreviewFile();
        });

        // 上傳按鈕點擊事件
        uploadButton.addClickListener(e -> {
            try {
                uploadSchedule();
            } catch (IOException ioException) {
                Notification.show("檔案上傳失敗", 3000, Notification.Position.MIDDLE);
            }
        });

        // 關閉按鈕點擊事件
        closeButton.addClickListener(e -> {
            closeDialog();
        });

        // 自動觸發關閉視窗時(例如點擊視窗外)，先清除預覽檔案
        addDialogCloseActionListener(e -> {
            closeDialog();
        });

    }

    private void previewFile() throws IOException {
        InputStream fileData = buffer.getInputStream();

        if (fileData == null) {
            Notification.show("無可預覽的檔案", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        // 讀取CSV內容
        List<List<String>> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 以逗號分割每行並新增到列表中
                String[] values = line.split(",");
                List<String> row = List.of(values);
                data.add(row);
            }
        }
        // 將CSV內容顯示在Grid
        filePreviewGrid.setItems(data);
        filePreviewGrid.setVisible(true);
    }

    private void uploadSchedule() throws IOException {
        // 檢查檔案是否存在
//        InputStream fileData = buffer.getInputStream();
//        if (fileData == null || buffer.getFileName().isEmpty()) {
//            Notification.show("請選擇一個要上傳的檔案", 3000, Notification.Position.MIDDLE);
//            return;
//        }
//
//        NameAddedByteArrayResource resource = new NameAddedByteArrayResource(buffer.getFileName(), fileData.readAllBytes());
//        ApiResponse<List<ShiftSchedules>> response = service.uploadShiftSchedules(resource);
//
//        if (response != null && response.getCode() == 0) {
//            Notification.show("上傳成功", 3000, Notification.Position.TOP_CENTER);
//            closeDialog();
//        } else {
//            Notification.show("上傳失敗", 3000, Notification.Position.TOP_CENTER);
//        }
    }

    private void closeDialog() {
        clearPreviewFile();
        close();
    }

    private void clearPreviewFile() {
        filePreviewGrid.setItems(new ArrayList<>());
        filePreviewGrid.setVisible(false);
        uploadButton.setEnabled(false);
    }

    private HorizontalLayout createButtonsLayout() {
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        uploadButton.setEnabled(false);
        closeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        return new HorizontalLayout(uploadButton, closeButton);
    }
}
