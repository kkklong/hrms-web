package com.hrm.application.views.notice;

import com.hrm.application.component.ConfirmDialog;
import com.hrm.application.entity.Notice;
import com.hrm.application.layout.MainLayout;
import com.hrm.application.menu.MenuRouter;
import com.hrm.application.model.Option;
import com.hrm.application.service.NoticeService;
import com.hrm.application.util.NotificationUtil;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Scope;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Scope("prototype")
@Route(value = "notice", layout = MainLayout.class)
@MenuRouter(label = "NoticeManager", icon = VaadinIcon.MEGAPHONE)
@PageTitle("公告管理 | HRM System")
public class NoticeView extends VerticalLayout {

    private NoticeService service;
    private Grid<Notice> grid = new Grid<>(Notice.class, false);
    private ListDataProvider<Notice> dataProvider;
    private ConfirmDialog confirmDialog = new ConfirmDialog();
    private NoticeDialog dialog;
    private Button batchDeleteButton = new Button("批量刪除");
    private TextField titleFilter = new TextField();
    private final List<Integer> selectedNoticeIds = new ArrayList<>();
    private List<Option<String>> noticeTypeOptionList;

    // ---- Add PageData ----
    private List<Notice> allNotices = new ArrayList<>();
    private int currentPage = 0;
    private int pageSize = 10;
    private ComboBox<Integer> pageSizeSelector = new ComboBox<>();
    private Button prevButton = new Button("上一頁");
    private Button nextButton = new Button("下一頁");
    private Span pageIndicator = new Span();

    public NoticeView(NoticeService service) {
        this.service = service;
        this.addClassName("background-plan");
        updateList();
        configureFilter();
        configureGrid();
        add(getTitle(), getToolBar(), getContent(), getPageTool());
        setSizeFull();
    }

    private HorizontalLayout getTitle() {
        HorizontalLayout titleHt = new HorizontalLayout();
        H3 title = new H3("NoticeManager");
        title.addClassName("title-heading");
        titleHt.add(title);
        titleHt.addClassName("title-config");
        titleHt.setWidthFull();
        return titleHt;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.addClassNames("grid-content");
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        // toggle Detail 內文
        grid.setDetailsVisibleOnClick(false);
        grid.addColumn(new ComponentRenderer<>(notice -> {
            Icon icon = grid.isDetailsVisible(notice) ? VaadinIcon.ANGLE_DOWN.create() : VaadinIcon.ANGLE_RIGHT.create();
            Button toggleButton = new Button(icon);
            toggleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            toggleButton.addClickListener(e -> {
                boolean currentlyVisible = grid.isDetailsVisible(notice);
                grid.setDetailsVisible(notice, !currentlyVisible);
                Icon newIcon = currentlyVisible ? VaadinIcon.ANGLE_RIGHT.create() : VaadinIcon.ANGLE_DOWN.create();
                toggleButton.setIcon(newIcon);
            });
            return toggleButton;
        })).setHeader("內文").setAutoWidth(true).setFlexGrow(0);
        grid.setItemDetailsRenderer(new ComponentRenderer<>(notice -> {
            Div contentDiv = new Div(new Text("<內文>:    " + notice.getContent().toString()));
            contentDiv.getStyle().set("padding", "1em");
            return contentDiv;
        }));
        // 標題欄位（點擊打開 Dialog）
        grid.addColumn(new ComponentRenderer<>(notice -> {
            Span title = new Span("編輯");
            title.getElement().addEventListener("click", e -> editNotice(notice));
            title.getStyle().set("cursor", "pointer").set("color", "var(--lumo-primary-text-color)");
            return title;
        })).setHeader("操作");
        grid.addColumn(Notice::getTitle).setHeader("標題");
        grid.addColumn(Notice::getType).setHeader("類別");
        Grid.Column<Notice> createdDate = grid.addColumn(Notice::getCreatedDate).setHeader("創建時間");
        grid.addColumn(Notice::getCreatedId).setHeader("創建者");
        grid.addColumn(Notice::getPublishDate).setHeader("發布時間");
        grid.addColumn(Notice::getEndDate).setHeader("撤銷時間");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.sort(List.of(new GridSortOrder<>(createdDate, SortDirection.DESCENDING)));
        grid.addColumn(new ComponentRenderer<>(notice -> {
            Checkbox toggle = new Checkbox();
            toggle.addClassName("my-switch");            // 根據 int 值設定 checkbox 狀態
            toggle.setValue(Optional.ofNullable(notice.getStatus()).orElse((byte) 0) == 1);            // 監聽狀態變更事件
            toggle.addValueChangeListener(event -> {
                Boolean success = (event.getValue() ? service.enableNotice(notice.getId()) : service.disableNotice(notice.getId()));
                if (success) {
                    updateList();
                    closeEditor();
                }
            });
            return toggle;
        })).setHeader("狀態");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addSelectionListener(selection -> {
            int size = selection.getAllSelectedItems().size();
            selection.getAllSelectedItems().forEach(item -> {
                selectedNoticeIds.clear();
                selectedNoticeIds.add(item.getId());
            });
            batchDeleteButton.setEnabled(size != 0);
        });
    }

    private void configureDialog() {
        dialog = new NoticeDialog(noticeTypeOptionList);
        dialog.addSaveListener(this::saveNotice);
        dialog.addUpdateListener(this::updateNotice);
        dialog.addSaveAndSendListener(this::saveAndSendNotice);
        dialog.addDeleteListener(this::deleteNotice);
        dialog.addCloseListener(e -> closeEditor());
        dialog.addDialogCloseActionListener(e -> closeEditor());

        confirmDialog.setConfirmAction(() -> batchDeleteNotice(selectedNoticeIds));
    }

    private void setData() {
        List<Option<Byte>> noticeStatusList = service.getNoticeStatusOptionList();
        noticeTypeOptionList = service.getNoticeTypeOptionList();
        pageSizeSelector.setItems(10, 20, 50, 100);
        pageSizeSelector.setValue(10);
    }

    private Component getToolBar() {
        Button createNoticeButton = new Button("新增公告");
        titleFilter.setPlaceholder("搜尋標題...");
        titleFilter.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        titleFilter.setValueChangeMode(ValueChangeMode.LAZY);
        titleFilter.setClearButtonVisible(true);
        batchDeleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        createNoticeButton.addClickListener(click -> createNotice());
        batchDeleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        batchDeleteButton.setEnabled(false);
        batchDeleteButton.addClickListener(click -> {
            confirmDialog.openDialogWithParameter("確認執行刪除?", "刪除");
        });

        var toolbar = new HorizontalLayout(titleFilter, createNoticeButton, batchDeleteButton);
        return toolbar;
    }

    private void updateList() {
        allNotices = service.getNoticeList();
        int fromIndex = currentPage * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allNotices.size());
        List<Notice> paginatedList = allNotices.subList(fromIndex, toIndex);

        dataProvider = new ListDataProvider<>(paginatedList);
        grid.setItems(dataProvider);
        int totalPages = (int) Math.ceil((double) allNotices.size() / pageSize);
        pageIndicator.setText("第 " + (currentPage + 1) + " / " + totalPages + " 頁");

        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);
        reApplyFilter();
    }

    private void saveNotice(NoticeDialog.SaveEvent event) {
        Notice notice = event.getNotice();
        MultiFileMemoryBuffer buffer = event.getBuffer();
        boolean success = false;

        if (buffer.getFiles().isEmpty()) {
            success = service.createNotice(notice, null, null);
        } else {
            String fileName = buffer.getFiles().iterator().next();
            InputStream inputStream = buffer.getInputStream(fileName);
            try {
                byte[] fileBytes = IOUtils.toByteArray(inputStream);
                success = service.createNotice(notice, fileBytes, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (success) {
            Notification.show("儲存成功");
            updateList();
            closeEditor();
        } else {
            Notification.show("儲存失敗");
        }
    }

    private void updateNotice(NoticeDialog.UpdateEvent event) {
        Notice notice = event.getNotice();
        MultiFileMemoryBuffer buffer = event.getBuffer();
        boolean success = false;

        if (buffer.getFiles().isEmpty()) {
            success = service.updateNotice(notice, null, null);
        } else {
            String fileName = buffer.getFiles().iterator().next();
            InputStream inputStream = buffer.getInputStream(fileName);
            try {
                byte[] fileBytes = IOUtils.toByteArray(inputStream);
                success = service.updateNotice(notice, fileBytes, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (success) {
            Notification.show("儲存成功");
            updateList();
            closeEditor();
        } else {
            Notification.show("儲存失敗");
        }
    }

    private void saveAndSendNotice(NoticeDialog.SaveAndSendEvent event) {
        Notice notice = event.getNotice();
        MultiFileMemoryBuffer buffer = event.getBuffer();
        boolean success = false;

        if (buffer.getFiles().isEmpty()) {
            success = service.saveAndSendNotice(notice, null, null);
        } else {
            String fileName = buffer.getFiles().iterator().next();
            InputStream inputStream = buffer.getInputStream(fileName);
            try {
                byte[] fileBytes = IOUtils.toByteArray(inputStream);
                success = service.saveAndSendNotice(notice, fileBytes, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (success) {
            Notification.show("儲存成功");
            updateList();
            closeEditor();
        } else {
            Notification.show("儲存失敗");
        }
    }

    private void deleteNotice(NoticeDialog.DeleteEvent event) {
        boolean success = service.deleteNotice(event.getNotice());
        if (success) {
            Notification.show("刪除成功");
            closeEditor();
            updateList();
        } else {
            Notification.show("刪除失敗");
        }
    }

    private void batchDeleteNotice(List<Integer> selectedNoticeIds) {
        if (selectedNoticeIds.isEmpty()) {
            Notification.show("未選取通知");
            return;
        }
        boolean success = service.deleteBatchNotice(selectedNoticeIds);
        if (success) {
            Notification.show("批量刪除成功");
            confirmDialog.close();
            updateList();
        } else {
            Notification.show("批量刪除失敗");
        }
    }

    public void editNotice(Notice notice) {
        if (notice == null) {
            closeEditor();
        } else {
            dialog.setDialogView(false);
            openEditor(notice);
        }
    }

    private void openEditor(Notice notice) {
        dialog.setNotice(notice);
        dialog.open();
    }

    private void closeEditor() {
        dialog.close();
        removeClassName("editing");
    }

    private void createNotice() {
        dialog.setDialogView(true);
        openEditor(new Notice());
    }

    private void configureFilter() {
        titleFilter.addValueChangeListener(event -> applyFilter());
        titleFilter.setClearButtonVisible(true);
    }

    private void applyFilter() {
        ListDataProvider<Notice> provider = (ListDataProvider<Notice>) grid.getDataProvider();
        provider.clearFilters();

        if (!titleFilter.isEmpty()) {
            provider.addFilter(notification -> notification.getTitle().toLowerCase().contains(titleFilter.getValue().toLowerCase()));
        }
    }

    private void reApplyFilter() {
        String currentValue = titleFilter.getValue();
        titleFilter.setValue(currentValue);
        applyFilter();
    }

    private Component getPageTool() {
        pageSizeSelector.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                pageSize = e.getValue();
                currentPage = 0; // Reset to first page
                updateList();
            }
        });
        pageSizeSelector.setWidth("8em");
        pageSizeSelector.setItemLabelGenerator(e -> e + "筆/頁");
        pageSizeSelector.getStyle().set("--vaadin-input-field-border-width", "1.5px");
        pageSizeSelector.getStyle().set("--vaadin-combo-box-overlay-width", "8em");
        prevButton.addClickListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateList();
            }
        });

        nextButton.addClickListener(e -> {
            int maxPage = (allNotices.size() - 1) / pageSize;
            log.info("allNotices.size()" + allNotices.size());
            if (currentPage < maxPage) {
                currentPage++;
                updateList();
            }
        });

        HorizontalLayout pagingControls = new HorizontalLayout(
                pageSizeSelector, prevButton, pageIndicator, nextButton
        );
        pagingControls.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        return pagingControls;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        ui.access(() -> {
            try {
                setData();
                configureDialog();
            } catch (Exception e) {
                NotificationUtil.error("載入資料失敗：" + e.getMessage());
            }
        });
    }
}
