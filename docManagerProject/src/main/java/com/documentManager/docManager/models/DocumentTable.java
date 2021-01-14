package com.documentManager.docManager.models;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentTable {

    private String name;
    private String api;
    private String filePath;

    public DocumentTable(String name, String api, String filePath) {
        this.name = name;
        this.api = api;
        this.filePath = filePath;
    }
}
