package com.documentManager.docManager.controllers;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentParser {

    public static Set<String> getKeyWords(XWPFDocument document, String keyword) {
        Set<String> toReturn = new HashSet<>();
        document.getParagraphs().stream().filter(p -> p.getText().contains(keyword)).forEach(p -> toReturn.add(p.getText().split("<change>")[1]));
        return toReturn;
    }

    public static List<String> getTableHeaders(XWPFDocument document) {
        List<String> toReturn = new ArrayList<>();
        for (XWPFTable table : document.getTables()) {
            String tableText = "";
                for (XWPFTableCell cell : table.getRow(0).getTableCells()) {
                    tableText += cell.getText() + " ";
                }
            toReturn.add(tableText);
        }
        return toReturn;
    }
}
