package com.documentManager.docManager.controllers;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentParser {

    public static void main(String[] args) throws IOException {
        File docxDoc = new File("ter_template.docx");
        FileInputStream fis = new FileInputStream(docxDoc.getAbsolutePath());
        XWPFDocument document = new XWPFDocument(fis);
        System.out.println(
                getKeyWords(document, "<change>"));
                getTableHeaders(document).forEach(t -> System.out.println(t));
    }

    public static Set<String> getKeyWords(XWPFDocument document, String keyword) {
        Set<String> toReturn = new HashSet<>();
        document.getParagraphs().stream().filter(p -> p.getText().contains(keyword)).forEach(p -> toReturn.add(p.getText().split("<change>")[1]));
        return toReturn;
    }

    public static List<String> getTableHeaders(XWPFDocument document) {
        List<String> toReturn = new ArrayList<>();
        document.getTables().forEach(table -> toReturn.add(table.getText().replace("\t", " - ").replace("\n", "")));
        return toReturn;
    }
}
