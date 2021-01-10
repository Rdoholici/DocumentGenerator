package com.documentManager.docManager.models;

import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Data
public class Document {

    private String name;
    private String user;
    private String path;
    private List<String> tables;
    private UserKeyword userKeyword;
    private DocumentType documentType;
    private List<MultipartFile> excelFiles;
    private List<Workbook> excelFilesWorkbook;
    private Set<String> keywords;
    private static Document document = new Document();

    public static Document getInstance() {
        return document;
    }

}
