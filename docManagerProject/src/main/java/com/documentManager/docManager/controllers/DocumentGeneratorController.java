package com.documentManager.docManager.controllers;

import com.aspose.words.FindReplaceDirection;
import com.aspose.words.FindReplaceOptions;
import com.documentManager.docManager.models.Document;
import com.documentManager.docManager.models.DocumentTable;
import com.documentManager.docManager.models.JiraTicket;
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

    public static void addExcelRowsToTableFromApi(String tableTitle, JiraTicket[] tickets) {
        List<IBodyElement> documentElementsList = xwpfDocument.getBodyElements();
        XWPFTable xwpfTable = null;
        for (int j = 0; j < documentElementsList.size(); j++) {
            if (tableTitle.isEmpty()) {
                continue;
            }
            if (documentElementsList.get(j).getElementType().toString().equals("PARAGRAPH") &&
                    ((XWPFParagraph) documentElementsList.get(j)).getText().equals(tableTitle)) {
                xwpfTable = (XWPFTable) documentElementsList.get(j + 1);
            }
        }

        for (JiraTicket jiraTicket : tickets) {
            //create row in word table
            XWPFTableRow row = xwpfTable.createRow();
            //iterate text from excel and add it

            List<String> rowCells = new ArrayList<>();
            rowCells.add(jiraTicket.getIssueType());
            rowCells.add(jiraTicket.getIssue_key());
            rowCells.add(jiraTicket.getDescription());
            rowCells.add(jiraTicket.getPriority());
            rowCells.add(jiraTicket.getSeverity());
            rowCells.add(jiraTicket.getStatus());

            for (int i = 0; i < rowCells.size(); i++) {
                row.getCell(i).setText(rowCells.get(i));
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
                String newRunText = runText.replaceAll("<change>", "");
                run.setText(newRunText.trim(), 0);
            }
        }
    }

    public static void sanitizeKeywords() {
        List<XWPFParagraph> allParas = xwpfDocument.getParagraphs();
        for (XWPFParagraph paragraph : allParas) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (int i = 0; i < runs.size(); i++) {
                //check if i is valid in range
                if (i + 1 > runs.size() || i + 2 >= runs.size()) {
                    continue;
                }

                //check if this run has <
                boolean hasBegin = runs.get(i).getText(0).contains("<");
                //if yes check if next run has change
                boolean hasMid = runs.get(i + 1).getText(0).contains("change");
                //if yes to both check if next run has >
                boolean hasEnd = runs.get(i + 2).getText(0).contains(">");

                //if yes to all set text to first run to be same but without last < | set text to middle run to be nothing | set text to last run to be same but without first <
                if (hasBegin && hasMid && hasEnd) {
                    String beginText = runs.get(i).getText(0);
                    beginText = beginText.substring(0, beginText.lastIndexOf("<"));
                    runs.get(i).setText(beginText, 0);

                    String endText = runs.get(i + 2).getText(0);
                    endText = endText.substring(1);
                    runs.get(i + 2).setText(endText, 0);

                    runs.get(i + 1).setText("", 0);
                }
            }
        }
    }

    public static void replaceKeywordsAspose(String textToReplace, String newValue, String path) throws Exception {
        com.aspose.words.Document doc = new com.aspose.words.Document(path);

        // Find and replace text in the document
        doc.getRange().replace(textToReplace, newValue, new FindReplaceOptions(FindReplaceDirection.FORWARD));

        // Save the Word document
        doc.save(path);
    }

    public static void replaceTextInAllParagraphs(String textToReplace, String newValue) {
        List<XWPFParagraph> para = xwpfDocument.getParagraphs().stream().filter(p -> p.getText().contains("<change>" + textToReplace + "<change>")).collect(Collectors.toList());

        for (XWPFParagraph paragraph : para) {
            //get the correct text
            String paragraphText = paragraph.getText();

            paragraphText = paragraphText.replaceAll("<change>" + textToReplace + "<change>", newValue.trim());

            //remove all runs
            int runs = paragraph.getRuns().size();
            for (int i = 0; i < runs; i++) {
                paragraph.removeRun(0);
            }
            //add new run
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(paragraphText);
        }

//        for (XWPFParagraph paragraph : para) {
//            List<XWPFRun> runs = paragraph.getRuns();
//            if (runs != null) {
//                for (XWPFRun run : runs) {
//                    String text = run.getText(0);
//                    if (text.contains(textToReplace)) {
//                        run.setText(newValue.trim(), 0);
//                    }
//                }
//            }
//        }
    }
}