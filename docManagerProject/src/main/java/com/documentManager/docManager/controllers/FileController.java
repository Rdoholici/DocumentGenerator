package com.documentManager.docManager.controllers;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class FileController {

    public static void saveFile(MultipartFile file, String pathToSave) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            Path path = Paths.get(pathToSave + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verifyFile(MultipartFile file, String expectedExtension) {
        if (file.isEmpty()) {
            return false;
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        //try - verify extension is as expected / catch - the file has no extension
        try {
            return fileName.substring(fileName.lastIndexOf(".")).equals(expectedExtension);
        } catch (Exception e) {}
        return false;
    }
}
