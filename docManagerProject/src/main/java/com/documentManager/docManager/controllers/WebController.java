package com.documentManager.docManager.controllers;

import com.documentManager.docManager.models.JiraTicket;
import com.documentManager.docManager.models.UIModelInfo;
import com.documentManager.docManager.services.JiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class WebController {
    @Autowired
    JiraService jiraService;

    @RequestMapping("/exampleUI")
    private String getConverterLength(Model model) {
        UIModelInfo modelInfo = new UIModelInfo();
        model.addAttribute("modelInfo", modelInfo);
//        Date dateAcum = new Date();
//        model.addAttribute("datadeacum", dateAcum.toString());
        return "indexuri";


        }
    @RequestMapping(value = "/exampleUI", method = RequestMethod.POST)
//    @ResponseBody

    private String returnNarID(@RequestParam(value = "narID") String narID, Model model) {
        System.out.println("111111 " + narID);
        List<JiraTicket> listOfJiraTicket= Arrays.asList(jiraService.getJiraTicketsById(narID));
        model.addAttribute("JiraTickets", listOfJiraTicket);
        return "jiraTickets";
//        return "http://localhost:8080/getJiraTicketsByNarID/{narID}";
    }
    }

