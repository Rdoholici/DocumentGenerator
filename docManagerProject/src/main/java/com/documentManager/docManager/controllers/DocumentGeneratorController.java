package com.documentManager.docManager.controllers;

import com.documentManager.docManager.models.Document;
import com.documentManager.docManager.models.DocumentTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
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
    public static boolean numberOfColWordExcelDiffer = true;


    public static boolean isNumberOfColWordExcelDiffer() {
        return numberOfColWordExcelDiffer;
    }

    public static void setNumberOfColWordExcelDiffer(boolean numberOfColWordExcelDiffer) {
        DocumentGeneratorController.numberOfColWordExcelDiffer = numberOfColWordExcelDiffer;
    }

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

        CTTblBorders borders = xwpfTable.getCTTbl().getTblPr().addNewTblBorders();
        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewTop().setVal(STBorder.SINGLE);
        //also inner borders
        borders.addNewInsideH().setVal(STBorder.SINGLE);
        borders.addNewInsideV().setVal(STBorder.SINGLE);
    }

    public static void setDocumentTemplate(String templatePath) throws IOException {
        File docxDoc = new File(templatePath);
        FileInputStream fis = new FileInputStream(docxDoc.getAbsolutePath());
        xwpfDocument = new XWPFDocument(fis);
    }

    public static void replaceTextInAllParagraphs(String textToReplace, String newValue) {
        List<XWPFParagraph> para = xwpfDocument.getParagraphs().stream().filter(p -> p.getText().contains("<change>" + textToReplace + "<change>")).collect(Collectors.toList());

        for (XWPFParagraph paragraph : para) {
            //get the correct text
            String paragraphText = paragraph.getText();
            paragraphText = paragraphText.replaceAll(textToReplace, newValue.trim());

            //remove all runs
            int runs = paragraph.getRuns().size();
            for (int i = 0; i < runs; i++) {
                paragraph.removeRun(0);
            }
            //add new run
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(paragraphText);
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


    public static void dataBindingIdExcel(String[] identifierList, MultipartFile[] userExcelFiles) throws Exception {

//        errorFlag = false;
//        message.add("You didnt insert any value for "+document.getKeywords().toArray()[i]);
//        attributes.addFlashAttribute("message", message);

        // iterate through the identifier list provided from front end
        for (int i = 0; i < identifierList.length; i++) {
            List<IBodyElement> documentElementsList = xwpfDocument.getBodyElements();
            for (int j = 0; j < documentElementsList.size(); j++) {
                if (identifierList[i].isEmpty()) {
                    continue;
                }
                if (documentElementsList.get(j).getElementType().toString().equals("PARAGRAPH") &&
                        ((XWPFParagraph) documentElementsList.get(j)).getText().equals(identifierList[i])) {

                    XWPFTable xwpfTable = (XWPFTable) documentElementsList.get(j + 1);
                    FileInputStream fileInputStream = null;

                    try {

                        if (userExcelFiles[i].getSize() != 0) {

//                            System.out.println("nu e null");
                            String TABLE_UPLOAD_DIR = "./uploads/tables/";
                            String filePath = FileController.saveFile(userExcelFiles[i], TABLE_UPLOAD_DIR);
                            File workbookFile = new File(filePath);
                            fileInputStream = new FileInputStream(workbookFile);
                            Workbook workbook = WorkbookFactory.create(fileInputStream);
                            //verificare daca excelul uploadat are acelasi numar de coloane ca si in word
                            //daca avem diferenta, nu ii incarcam acel excel.
                            //number of columns from word document
                            int noOfWordColumns = xwpfTable.getRow(0).getTableCells().size();
                            Sheet sheet = workbook.getSheetAt(0);
                            //number of columns from excel document
                            int noOfExcelColumns = sheet.getRow(0).getPhysicalNumberOfCells();

                            if (noOfWordColumns == noOfExcelColumns) {
                                setNumberOfColWordExcelDiffer(true);
                                addExcelRowsToTable(xwpfTable, workbook);
                            } else {
                                setNumberOfColWordExcelDiffer(false);
                            }
                        } else {
                            System.out.println("e null");
                        }
                    } finally {
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                    }
                }
            }
        }
    }

    public static void sanitizeTags() {
        List<XWPFParagraph> allParas = xwpfDocument.getParagraphs();
        for (XWPFParagraph para : allParas) {
            List<XWPFRun> runs = para.getRuns();
            for (XWPFRun run : runs) {
                String runText = run.getText(0);
                String newRunText = runText.replaceAll("<change>","");
                run.setText(newRunText.trim(),0);
            }
        }
    }
}