package com.hrm.application.component;

import com.hrm.application.entity.FileData;
import com.hrm.application.util.BEClientUtil;
import com.hrm.application.util.ToolUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.server.StreamResource;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileListForm extends FormLayout {
    private Map<String, Button> previewButtons = new HashMap<>();
    private final Map<String, StreamResource> streamResources = new HashMap<>();

    public FileListForm() {
    }

    public void handleFileUpload(String fileName, InputStream inputStream, ImagePreviewLayout imagePreviewLayout) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            inputStream.transferTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        StreamResource streamResource = ToolUtil.toStreamResource(fileName, outputStream.toByteArray());
        createAndAddPreviewButton(fileName, streamResource, imagePreviewLayout);
    }

    public void handleFileRemove(String removedFileName) {
        if (removedFileName != null) {
            Button removedButton = previewButtons.remove(removedFileName);
            if (removedButton != null) {
                this.remove(removedButton);
            }
        }
    }

    public void displayFiles(List<FileData> fileList, ImagePreviewLayout imagePreviewLayout) {
        if (fileList != null) {
            for (FileData file : fileList) {
                StreamResource streamResource = new StreamResource(file.getFileName(), () -> {
                    BEClientUtil beClientUtil = new BEClientUtil(WebClient.builder().build());
                    byte[] fileBytes = beClientUtil.doGetFileBinary(file.getFileUrl(), null, null);
                    return new ByteArrayInputStream(fileBytes);
                });
                streamResources.put(file.getFileName(), streamResource);
                createAndAddPreviewButton(file.getFileName(), streamResource, imagePreviewLayout);
            }
        }
    }

    private void createAndAddPreviewButton(String fileName, StreamResource streamResource, ImagePreviewLayout imagePreviewLayout) {
        Button previewButton = new Button(fileName, click ->
                imagePreviewLayout.showPreview(fileName, streamResource));
        previewButton.getStyle().set("--vaadin-button-border", "1px solid");
        previewButtons.put(fileName, previewButton);
        this.add(previewButton);
    }
}
