package com.hrm.application.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

public class ImagePreviewLayout extends VerticalLayout {

    private H4 title;
    private Image imagePreview;
    private Anchor downloadLink;

    public ImagePreviewLayout() {
        this.setSizeFull();
        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.add(getTitle(), getImageArea(), getPreviewButton());
        // 預設隱藏
        hidePreview();
    }

    private H4 getTitle() {
        title = new H4();
        title.getStyle().set("padding", "0px");
        return title;
    }

    /**
     * 取得預覽限制範圍
     */
    private VerticalLayout getImageArea() {

        // 垂直佈局水平置中
        VerticalLayout previewArea = new VerticalLayout();
        previewArea.setWidth("40em");
        previewArea.setHeight("42em");
        previewArea.setPadding(false);
        previewArea.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // 水平佈局垂直置中
        HorizontalLayout previewHtArea = new HorizontalLayout();
        // 不超過限制範圍
        previewHtArea.setMaxWidth("100%");
        // 填滿高度，讓物件可以垂直置中
        previewHtArea.setHeight("100%");
        previewHtArea.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        previewArea.add(previewHtArea);

        // 圖片
        imagePreview = new Image();
        imagePreview.setAlt("Image Preview");
        // 圖片不超過限制範圍
        imagePreview.setMaxWidth("100%");
        imagePreview.setMaxHeight("100%");
        previewHtArea.add(imagePreview);

        return previewArea;
    }

    private HorizontalLayout getPreviewButton() {
        downloadLink = new Anchor();

        Button downloadButton = new Button("下載附件");
        downloadButton.setWidth("10em");
        downloadButton.getStyle().set("--vaadin-button-border", "1px solid");
        downloadButton.addClickListener(event -> downloadLink.getElement().callJsFunction("click"));

        Button closeButton = new Button("關閉預覽", event -> hidePreview());
        closeButton.getStyle().set("--vaadin-button-border", "1px solid");
        closeButton.setWidth("10em");
//        closeButton.addClickShortcut(Key.ESCAPE);

        HorizontalLayout buttonLayout = new HorizontalLayout(downloadLink, downloadButton, closeButton);
        buttonLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        buttonLayout.getStyle().set("padding", "0px");
        return buttonLayout;
    }

    public void hidePreview() {
        this.setVisible(false);
    }

    public void showPreview(String fileName, StreamResource streamResource) {
        title.setText("檔案預覽: " + fileName);
        if (hasImage(fileName)) {
            imagePreview.setSrc(streamResource);
        }
        downloadLink.setHref(streamResource);
        downloadLink.getElement().setAttribute("download", fileName);
        this.setVisible(true); // 顯示預覽區域
    }

    private static boolean hasImage(String fileName) {
        return fileName.endsWith(".jpg") || fileName.endsWith(".png");
    }

}