package com.documentManager.docManager.controllers;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReaderController {

    public static void main(String[] args) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(new File("jira_excel_example.xlsx"));
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();
//        https://www.callicoder.com/java-read-excel-file-apache-poi/

        List<String> tickets = new ArrayList<>();
        for (Row row : sheet) {
//    System.out.println(row.getCell(5));
//    System.out.println(row.getCell(5).equals("In testing"));
            if (dataFormatter.formatCellValue(row.getCell(5)).equals("In testing") ) {
                tickets.add(dataFormatter.formatCellValue(row.getCell(1)));
            }
        }

        tickets.forEach(
                ticket -> System.out.println(ticket)
        );


    }
}
