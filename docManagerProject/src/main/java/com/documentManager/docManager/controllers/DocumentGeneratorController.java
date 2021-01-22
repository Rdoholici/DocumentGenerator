package com.documentManager.docManager.controllers;

import com.documentManager.docManager.models.Document;
import com.documentManager.docManager.models.DocumentTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentGeneratorController {
    private static XWPFDocument xwpfDocument;


    public static void saveDocument(String outputFileName) throws IOException {
        FileOutputStream out = new FileOutputStream(outputFileName);
        xwpfDocument.write(out);
        out.close();
        xwpfDocument.close();
    }

    public static void addExcelRowsToTable(XWPFTable xwpfTable, Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheetAt(0);

        //iterate excel file rows
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            //for each row build list of text cells
            List<String> rowtext = new ArrayList<>();
            Iterator<Cell> cells = sheet.getRow(i).cellIterator();
            while (cells.hasNext()) {
                rowtext.add(cells.next().toString());
            }

            //create row in word table
            XWPFTableRow row = xwpfTable.createRow();
            //iterate text from excel and add it
            for (int j = 0; j < rowtext.size(); j++) {
                row.getCell(j).setText(rowtext.get(j));
            }
        }
    }

    public static void setDocumentTemplate(String templatePath) throws IOException {
        File docxDoc = new File(templatePath);
        FileInputStream fis = new FileInputStream(docxDoc.getAbsolutePath());
        xwpfDocument = new XWPFDocument(fis);
    }

    public static void replaceTextInAllParagraphs(String textToReplace, String newValue) {
        List<XWPFParagraph> para = xwpfDocument.getParagraphs().stream().filter(p -> p.getText().contains(textToReplace)).collect(Collectors.toList());
        for (XWPFParagraph paragraph : para) {
            List<XWPFRun> runs = paragraph.getRuns();
            if (runs != null) {
                for (XWPFRun run : runs) {
                    String text = run.getText(0);
                    if (text.contains(textToReplace)) {
                        run.setText(newValue, 0);
                    }
                }
            }
        }

    }

    private static List<XWPFTable> findTablesByHeader(String header) {
        return xwpfDocument.getTables().stream().filter(
                table -> table.getText().replace("\t", "").replace("\n", "").replace(" ", "")
                        .contains(header.replace(" ", ""))).collect(Collectors.toList());
    }

    public static void addExcelTableToDocumentTable(DocumentTable documentTable) throws Exception {
        Workbook workbook = WorkbookFactory.create(new File(documentTable.getFilePath()));
        Document document = Document.getInstance();
        //iterate document tables and find the tables with the same header
        List<XWPFTable> possibleTables = findTablesByHeader(documentTable.getName());

        addExcelRowsToTable(possibleTables.get(0), workbook); //TODO which possible table to use?
        workbook.close();


    }

    public static void dataBindingIdExcel(String[] identifierList , MultipartFile[] userExcelFiles) throws Exception {

        for(int i=0;i<identifierList.length;i++){
            List<IBodyElement> documentElementsList = xwpfDocument.getBodyElements();
            for(int j=0;j<documentElementsList.size();j++){
                if(documentElementsList.get(j).getElementType().toString().equals("PARAGRAPH") &&
                        ((XWPFParagraph) documentElementsList.get(j)).getText().equals(identifierList[i])) {

                    XWPFTable xwpfTable = (XWPFTable) documentElementsList.get(j+1);
                    FileInputStream fileInputStream = null;

                    try {

                        if(userExcelFiles[i].getSize()!=0) {

//                            System.out.println("nu e null");

                            String TABLE_UPLOAD_DIR = "./uploads/tables/";
                            String filePath = FileController.saveFile(userExcelFiles[i], TABLE_UPLOAD_DIR);
                            File workbookFile = new File(filePath);
                            fileInputStream = new FileInputStream(workbookFile);
                            Workbook workbook = WorkbookFactory.create(fileInputStream);

                            //number of columns from word document
                            int noOfWordColumns = xwpfTable.getRow(0).getTableCells().size();
                            Sheet sheet = workbook.getSheetAt(0);
                            //number of columns from excel document
                            int noOfExcelColumns = sheet.getRow(0).getPhysicalNumberOfCells();

                            if(noOfWordColumns==noOfExcelColumns) {
                                addExcelRowsToTable(xwpfTable, workbook);
                            }
                            else {
                                System.out.println("different number of columns excel vs words");
                            }

                        }
                        else {
                            System.out.println("e null");
                        }
                    } finally {
                        if(fileInputStream!=null){
                            fileInputStream.close();
                        }
                    }
                }
            }
        }
    }
}