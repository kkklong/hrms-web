package com.hrm.application.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 公告標題
     */
    private String title;

    /**
     * 內容
     */
    private String content;

    /**
     * 公告發布時間
     */
    private LocalDateTime publishDate;

    /**
     * 公告結束時間
     */
    private LocalDateTime endDate;

    /**
     * 0:未發布、1:已發布、2:已撤銷
     */
    private Byte status;

    /**
     * 例如:一般通知、緊急通知、活動公告等
     */
    private String type;

    /**
     * 創建時間
     */
    private LocalDateTime createdDate;

    private String createdId;

    private List<FileData> files;
}
