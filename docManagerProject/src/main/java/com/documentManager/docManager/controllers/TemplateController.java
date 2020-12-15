package com.documentManager.docManager.controllers;

import com.documentManager.docManager.models.ALMReleaseInfo;
import com.documentManager.docManager.models.JiraTicket;
import com.documentManager.docManager.services.ALMService;
import com.documentManager.docManager.services.ConfluenceService;
import com.documentManager.docManager.services.JiraService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class TemplateController {

    @Autowired
    private JiraService jiraService;

    @Autowired
    private ConfluenceService confluenceService;

    @Autowired
    private ALMService almService;

    private void addRowOfJiraTicketsForId(List<JiraTicket> jiraTickets) {
        for (JiraTicket jiraTicket : jiraTickets) {
            List<String> rowCells = new ArrayList<>();
            rowCells.add(jiraTicket.getIssueType());
            rowCells.add(jiraTicket.getIssue_key());
            rowCells.add(jiraTicket.getDescription());
            rowCells.add(jiraTicket.getPriority());
            rowCells.add(jiraTicket.getSeverity());
            rowCells.add(jiraTicket.getStatus());
            DocumentGeneratorController.addRowToTable("Issue type - Key - Summary - Priority - Severity - Status", rowCells);
        }
    }

    private void getAlmInfo(String releaseNo) {
        ALMReleaseInfo almInfo = almService.getALMReleaseInfoByReleaseNo(releaseNo);
        List<String> almCells = new ArrayList<>();

        almCells.add(almInfo.getFunction());
        almCells.add(almInfo.getTotalTestCases().toString());
        almCells.add(almInfo.getPassedTestCases().toString() + ", " +
                (almInfo.getPassedTestCases() * 100) / almInfo.getTotalTestCases()
                + "%");
    }



    @GetMapping("/generateTER/{id}/{releaseNo}")
    public void generateTER(@PathVariable String id, @PathVariable String releaseNo) throws IOException, InvalidFormatException {
        DocumentGeneratorController.setDocumentTemplate("ter_template.docx");

        List<JiraTicket> jiraTickets = null;
        if (!"0".equals(id)) {
            jiraTickets = Arrays.asList(jiraService.getJiraTicketsById(id));
        } else {
            jiraTickets = ExcelReaderController.extractJiraTicketsFromExcelFile("jira_excel_example.xlsx");
        }
        addRowOfJiraTicketsForId(jiraTickets);

        List<String> almCells = new ArrayList<>();
        //almCells.add(almService.getALMReleaseInfoByReleaseNo(releaseNo).getReleaseNo());
        Integer totalTestCases = almService.getALMReleaseInfoByReleaseNo(releaseNo).getTotalTestCases();
        Integer passedTestCases = almService.getALMReleaseInfoByReleaseNo(releaseNo).getPassedTestCases();
        Integer failedTestCases = almService.getALMReleaseInfoByReleaseNo(releaseNo).getFailedTestCases();
        Integer blockedTestCases = almService.getALMReleaseInfoByReleaseNo(releaseNo).getBlockedTestCases();
        Integer notCompletedTestCases = almService.getALMReleaseInfoByReleaseNo(releaseNo).getNotCompletedTestCases();

        int percentagePassedTCs = (passedTestCases * 100) / totalTestCases;
        int percentageFailedTCs = (failedTestCases * 100) / totalTestCases;
        int percentageBlockedTCs = (blockedTestCases * 100) / totalTestCases;
        int percentageNotCompletedTCs = (notCompletedTestCases * 100) / totalTestCases;

        almCells.add(almService.getALMReleaseInfoByReleaseNo(releaseNo).getFunction());
        almCells.add(almService.getALMReleaseInfoByReleaseNo(releaseNo).getTotalTestCases().toString());
        almCells.add(almService.getALMReleaseInfoByReleaseNo(releaseNo).getPassedTestCases().toString() + ", " + percentagePassedTCs + "%");
        almCells.add(almService.getALMReleaseInfoByReleaseNo(releaseNo).getNotCompletedTestCases().toString() + ", " + percentageNotCompletedTCs + "%");
        almCells.add(almService.getALMReleaseInfoByReleaseNo(releaseNo).getFailedTestCases().toString() + ", " + percentageFailedTCs + "%");
        almCells.add(almService.getALMReleaseInfoByReleaseNo(releaseNo).getBlockedTestCases().toString() + ", " + percentageBlockedTCs + "%");

        DocumentGeneratorController.addRowToTable("Function/modules - Total tcs - Tcs passed - Tcs not completed - Tcs failed - Tcs blocked", almCells);

        DocumentGeneratorController.replaceTextInParagraph("RELEASE_NO", confluenceService.getConfluenceReleaseInfo(id).getReleaseNumber());
        DocumentGeneratorController.replaceTextInParagraph("TITLE", confluenceService.getConfluenceReleaseInfo(id).getAppName());
        DocumentGeneratorController.replaceTextInParagraph("RELEASE_DATE", confluenceService.getConfluenceReleaseInfo(id).getReleaseDate());
        DocumentGeneratorController.replaceTextInParagraph("REGION", confluenceService.getConfluenceReleaseInfo(id).getRegion());

        DocumentGeneratorController.saveDocument("modified.docx");
    }

}
