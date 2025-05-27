package com.hrm.application.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileData {

    private int fileId;

    private String fileName;

    private String fileUrl;
}