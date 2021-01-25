package com.documentManager.docManager.controllers;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentParser {

    public static Set<String> getKeyWords(XWPFDocument document, String keyword) {
        Set<String> toReturn = new HashSet<>();
        for (XWPFParagraph xwpfParagraph : document.getParagraphs()) {
            Pattern p = Pattern.compile(keyword + "(.*?)" + keyword, Pattern.DOTALL);
            Matcher m = p.matcher(xwpfParagraph.getText());
            while (m.find()) {
                toReturn.add(m.group(1));
            }
        }
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

    public static List<String> getTableTitles(XWPFDocument document) {

        java.util.List<XWPFParagraph> paragraphs =  document.getParagraphs();
        List<String> titlesToReturn = new ArrayList<>();
        for (XWPFParagraph paragraph: paragraphs){
            if(paragraph.getText().substring(0, Math.min(paragraph.getText().length(), 5)).equals("Table")) {
                titlesToReturn.add(paragraph.getText());
            }
        }
                return titlesToReturn;
    }
}
