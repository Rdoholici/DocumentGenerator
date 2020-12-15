package com.documentManager.docManager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
public class WebController {

    @RequestMapping("/exampleUI")
    private String getConverterLength(Model model) {
        Date dateAcum = new Date();
        model.addAttribute("datadeacum", dateAcum.toString());
        return "indexuri";
    }
}
