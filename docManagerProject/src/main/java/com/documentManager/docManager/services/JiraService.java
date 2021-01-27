package com.documentManager.docManager.services;

import com.documentManager.docManager.models.JiraTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JiraService {

    @Autowired
    private RestTemplate restTemplate;

    public JiraTicket[] getJiraTicketsById(String id) {
        return restTemplate.getForObject(String.format("http://localhost:8181/getJiraTicketsByNarID/%s",id), JiraTicket[].class);
//        http://localhost:8181/getJiraTicketsByNarID/1234
    }
}
