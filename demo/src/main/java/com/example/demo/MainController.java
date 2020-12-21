package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Controller
@ControllerAdvice
public class MainController {

    //file location
    private final String UPLOAD_DIR = "./uploads/";

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
    public String homepage() {
        return "index";
    }

    @PostMapping("/page1")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes, Model model) {

        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }

        // normalize the file path
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        //check if its a word document
        if(!(fileName.substring(fileName.lastIndexOf("."))).equals(".docx")){
            attributes.addFlashAttribute("message", "Only word documents are allowed.");
            return "redirect:/";
        }

        // save the file on the local file system and add a unique id at the end so that
        // users do not overwrite files between themselves
        try {
            Path path = Paths.get(UPLOAD_DIR + randId+"_"+fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                errorFlag = true;
                progressBar = 50;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set the progressbar and errorflag and return success message
        model.addAttribute("progressBar",progressBar);
        model.addAttribute("errorFlag",errorFlag);
        model.addAttribute("message","You successfully uploaded " + fileName + '!');
        return "page1";
    }

}

