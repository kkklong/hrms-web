package com.hrm.application.model;

import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.ByteArrayResource;

public class NameAddedByteArrayResource extends ByteArrayResource {

    private final static String DEFAULT_FILE_NAME = "file.txt";

    private final String fileName;

    public NameAddedByteArrayResource(String fileName, @NotNull byte[] byteArray) {
        super(byteArray);
        this.fileName = Strings.isBlank(fileName) ? DEFAULT_FILE_NAME : fileName;
    }

    public NameAddedByteArrayResource(String fileName, byte[] byteArray, String description) {
        super(byteArray, description);
        this.fileName = Strings.isBlank(fileName) ? DEFAULT_FILE_NAME : fileName;
    }

    @Override
    public String getFilename() {
        return fileName;
    }
}
