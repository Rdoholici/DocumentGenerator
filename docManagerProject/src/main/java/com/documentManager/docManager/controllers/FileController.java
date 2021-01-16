package com.documentManager.docManager.controllers;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class FileController {
    //file location
    private static final String UPLOAD_DIR = "./uploads/";
    private static String TEMPL_UPLOAD_DIR = "./uploads/templates";
    private static String TABLE_UPLOAD_DIR = "./uploads/tables/";
    private static String USER_UPLOAD_DIR = "./uploads/userUploads/";
    private static String RESULT = "./uploads/result";

    public static String saveFile(MultipartFile file, String pathToSave) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            Path path = Paths.get(pathToSave + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return path.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verifyFile(MultipartFile file, String expectedExtension) {
        if (file.isEmpty()) {
            return false;
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        //try - verify extension is as expected / catch - the file has no extension
        try {
            return fileName.substring(fileName.lastIndexOf(".")).contains(expectedExtension);
        } catch (Exception e) {
        }
        return false;
    }

    public static Sheet convertExcelToTable(String filePath) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(new File(filePath));
        return workbook.getSheetAt(0);
    }

    public static XWPFDocument convertWordToXWPFDocument(String filePath) throws IOException {
        File docxDoc = new File(filePath);
        FileInputStream fis = new FileInputStream(docxDoc.getAbsolutePath());
        return new XWPFDocument(fis);
    }

    public static void deleteTempFiles() {
        File excels = new File(TABLE_UPLOAD_DIR);
        for (File file : excels.listFiles()) {
            file.delete();
        }
        File userUploads = new File(USER_UPLOAD_DIR);
        for (File file : userUploads.listFiles()) {
            file.delete();
        }
    }
}
