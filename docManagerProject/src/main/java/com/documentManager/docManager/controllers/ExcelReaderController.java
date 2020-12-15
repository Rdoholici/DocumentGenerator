package com.documentManager.docManager.controllers;

import com.documentManager.docManager.models.JiraTicket;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReaderController {

    /**
     * Method to get jira tickets from a provided excel file.
     *
     * @param filePath the location of the excel file.
     * @return a list of jira tickets serialized from the excel file.
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static List<JiraTicket> extractJiraTicketsFromExcelFile(String filePath) throws IOException, InvalidFormatException {
        List<JiraTicket> toReturn = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(new File(filePath));
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();

        //go through each row in the sheet
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            toReturn.add(
                    new JiraTicket(
                            dataFormatter.formatCellValue(sheet.getRow(i).getCell(0)),
                            dataFormatter.formatCellValue(sheet.getRow(i).getCell(1)),
                            dataFormatter.formatCellValue(sheet.getRow(i).getCell(2)),
                            dataFormatter.formatCellValue(sheet.getRow(i).getCell(4)),
                            dataFormatter.formatCellValue(sheet.getRow(i).getCell(3)),
                            dataFormatter.formatCellValue(sheet.getRow(i).getCell(5))
                    ));
        }
        return toReturn;
    }
}
