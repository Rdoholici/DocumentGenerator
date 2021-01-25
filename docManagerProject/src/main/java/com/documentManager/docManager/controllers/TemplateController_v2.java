package com.documentManager.docManager.controllers;

import com.documentManager.docManager.models.JiraTicket;
import com.documentManager.docManager.services.JiraService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class TemplateController_v2 {


    public static void main(String[] args) throws IOException, InvalidFormatException {
        DocumentGeneratorController.setDocumentTemplate("ter_template.docx");
        HashMap<String, String> newValue = new HashMap<>();
        newValue.put("RELEASE_NO", "N1234");
        newValue.put("NAR_ID", "mockNar");
        replaceKeywordsInTemplate(newValue);
        cleanUpTag();
        DocumentGeneratorController.saveDocument("modified2.docx");
    }
    @Autowired
    JiraService jiraService;



    public TemplateController_v2() throws IOException {
    }

    public static void replaceKeywordsInTemplate(HashMap<String,String> newValue) throws IOException, InvalidFormatException { //newValue ar fi valoarea introdusa de la tastatura

        for ( String keyword : newValue.keySet()) {
            DocumentGeneratorController.replaceTextInAllParagraphs(keyword, newValue.get(keyword));
        }

    }
    public static void cleanUpTag() {
        DocumentGeneratorController.replaceTextInAllParagraphs("$$", "");

    }

    public void updateTableFromExcelOrApi (String filePath,String id, String tableHeader) throws IOException, InvalidFormatException {


        List<JiraTicket> jiraTickets=new ArrayList<>();
        //filePath ia valoarea documentului din UI
        if(filePath != null){
        jiraTickets= ExcelReaderController.extractJiraTicketsFromExcelFile(filePath);
        } else {
            jiraTickets= Arrays.asList(jiraService.getJiraTicketsById(id));
        }
        addRowOfJiraTicketsForId(jiraTickets,tableHeader);

    }

    private void addRowOfJiraTicketsForId(List<JiraTicket> jiraTickets, String tableHeader) { //tableHeader vine din UI
        for (JiraTicket jiraTicket : jiraTickets) {
            List<String> rowCells = new ArrayList<>();
            rowCells.add(jiraTicket.getIssueType());
            rowCells.add(jiraTicket.getIssue_key());
            rowCells.add(jiraTicket.getDescription());
            rowCells.add(jiraTicket.getPriority());
            rowCells.add(jiraTicket.getSeverity());
            rowCells.add(jiraTicket.getStatus());
//            DocumentGeneratorController.addRowToTable(tableHeader, rowCells);
        }
    }

//    public void generateTer(String newValue, String filePath, String id, String tableHeader) throws IOException, InvalidFormatException {
//        replaceKeywordsInTemplate(newValue);
//        updateTableFromExcelOrApi(filePath, id, tableHeader);
//        DocumentGeneratorController.saveDocument("modified.docx");
//    }

}
