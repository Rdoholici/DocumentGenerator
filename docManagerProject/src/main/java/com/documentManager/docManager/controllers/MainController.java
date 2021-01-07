package com.documentManager.docManager.controllers;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@ControllerAdvice
public class MainController {

    //file location
    private final String UPLOAD_DIR = "./uploads/";
    private String TEMPL_UPLOAD_DIR = "./uploads/templates";

    private Path path;
    private Path templPath;
    //variable for file name manipulation

    UUID randId = UUID.randomUUID();

    //variables for html manipulation
    boolean errorFlag = true;
    int progressBar = 0;


    //Check file size:
    //StandardServletMultipartResolver
    @ExceptionHandler(MultipartException.class)
    public String handleError1(MultipartException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
        errorFlag = false;
        return "redirect:/";

    }

    //CommonsMultipartResolver
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleError2(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("message", "The field file exceeds its maximum permitted size of 1048576 bytes.");
        errorFlag = false;
        return "redirect:/";

    }

    @GetMapping("/")
    public String homepage(Model model) {

        DocumentType documentType = new DocumentType();
        model.addAttribute("documentType", documentType);

        List<String> listType = Arrays.asList("TER - release", "TER - Hot Fix");
        model.addAttribute("listType", listType);
        return "index";
    }


    @PostMapping("/page1") //for new ter from user
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes, Model model) throws IOException {


        // normalize the file path
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        //check file to exist and be docx type
        if (!FileController.verifyFile(file, ".docx")) {
            attributes.addFlashAttribute("message", "Please select a valid docx file to upload.");
            return "redirect:/";
        }

        FileController.saveFile(file, UPLOAD_DIR);
        // save the file on the local file system and add a unique id at the end so that
        // users do not overwrite files between themselves
//        try {
//            path = Paths.get(UPLOAD_DIR + fileName);
//            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//            errorFlag = true;
//            progressBar = 50;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        File docxDoc = new File(path.toString());
        FileInputStream fis = new FileInputStream(docxDoc.getAbsolutePath());
        XWPFDocument document = new XWPFDocument(fis);

        UserKeyword userKeyword = new UserKeyword();
        model.addAttribute("userKeyword", userKeyword);

        // set the progressbar and errorflag and return success message
        model.addAttribute("progressBar", progressBar);
        model.addAttribute("errorFlag", errorFlag);
        model.addAttribute("message", "You successfully uploaded " + fileName + '!');
        model.addAttribute("keywords", DocumentParser.getKeyWords(document, "<change>"));
        model.addAttribute("tableHeaders", DocumentParser.getTableHeaders(document));
        return "page1";
    }

    @GetMapping("/result")
    public String showResult(Model model) {
        UserKeyword userKeyword = new UserKeyword();
        model.addAttribute("userKeyword", userKeyword);
        return "result";
    }


    @PostMapping("/result")
    public String submitForm(@ModelAttribute("userKeyword") UserKeyword userKeyword, MultipartFile[] files, Model model) {
        progressBar = 100;
        model.addAttribute("progressBar", progressBar);
        return "result";
    }

    @PostMapping("/existingTemplate")
    public String submitForm(@ModelAttribute("documentType") DocumentType documentType, Model model) throws IOException {

        progressBar = 50;
        model.addAttribute("progressBar", progressBar);
        if (documentType.getName().equals("TER - release")) {
            templPath = Paths.get(TEMPL_UPLOAD_DIR + "\\ter_release.docx");
            System.out.println(templPath);
            System.out.println("OK");
        } else if (documentType.getName().equals("TER - Hot Fix")) {
            templPath = Paths.get(TEMPL_UPLOAD_DIR + "\\ter_hotfix.docx");
            System.out.println(templPath);
            System.out.println("OK");
        }

        File docxDoc = new File(templPath.toString());
        FileInputStream fis = new FileInputStream(docxDoc.getAbsolutePath());
        XWPFDocument document = new XWPFDocument(fis);

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(docxDoc.getName()));
        errorFlag = true;

        UserKeyword userKeyword = new UserKeyword();

        model.addAttribute("userKeyword", userKeyword);
        model.addAttribute("errorFlag", errorFlag);
        model.addAttribute("keywords", DocumentParser.getKeyWords(document, "<change>"));
        model.addAttribute("tableHeaders", DocumentParser.getTableHeaders(document));
        model.addAttribute("message", "You successfully selected " + fileName + " template!");

// testare ca documentul este bine selectat
//        List<XWPFParagraph> data = document.getParagraphs();
//
//        for(XWPFParagraph p : data) {
//            System.out.print(p.getText());
//        }
//
//        System.out.println(docxDoc.length());
        return "extempresult";
    }
}

