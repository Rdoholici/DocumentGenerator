package com.api.reportGenerator.model;

import lombok.Data;

@Data
public class JiraTicket {
    private String issueType;
    private String issue_key;
    private String description;
    private String severity;
    private String priority;
    private String status;

    public JiraTicket(String issueType, String issue_key, String description, String severity, String priority, String status) {
        this.issueType = issueType;
        this.issue_key = issue_key;
        this.description = description;
        this.severity = severity;
        this.priority = priority;
        this.status = status;
    }
}
