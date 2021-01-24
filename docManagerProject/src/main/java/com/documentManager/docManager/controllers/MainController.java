package com.documentManager.docManager.controllers;

import com.documentManager.docManager.models.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//import jdk.internal.joptsimple.internal.Strings;

//import com.documentManager.docManager.models.UserKeyword;

@Controller
@ControllerAdvice
public class MainController {

    //file location
    private final String UPLOAD_DIR = "./uploads/";
    private String TEMPL_UPLOAD_DIR = "./uploads/templates/";
    private String TABLE_UPLOAD_DIR = "./uploads/tables/";
    private String USER_UPLOAD_DIR = "./uploads/userUploads/";
    private String RESULT = "./uploads/results/";



    private Path templPath;
    Document document = Document.getInstance();
    //variable for file name manipulation

    //variables for html manipulation and errors
    boolean errorFlag = true;
    Set<String> message= new HashSet<String>();

    int progressBar = 0;


    //Check file size:
    //StandardServletMultipartResolver
    @ExceptionHandler(MultipartException.class)
    public String handleError1(MultipartException e, RedirectAttributes redirectAttributes) {
        message.add(e.getCause().getMessage());
        redirectAttributes.addFlashAttribute("message", message);
        errorFlag = false;
        return "redirect:/";
    }

    //CommonsMultipartResolver
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleError2(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {
        message.add("The field file exceeds its maximum permitted size of 1048576 bytes.");
        redirectAttributes.addFlashAttribute("message", message);
        errorFlag = false;
        return "redirect:/";
    }

    @GetMapping("/")
    public String homepage(Model model) {
        DocumentType documentType = new DocumentType();
        model.addAttribute("documentType", documentType);
        model.addAttribute("errorFlag", false);
        List<String> listType = Arrays.asList("TER - release", "TER - Hot Fix");//TODO per user entries
        model.addAttribute("listType", listType);
        return "index";
    }

    @GetMapping("/page1")
        public String showPage(Model model,@ModelAttribute("userInput") UserInput userInput,RedirectAttributes attributes) {


        progressBar = 50;

        model.addAttribute("progressBar", progressBar);
        model.addAttribute("errorFlag", errorFlag);
        model.addAttribute("keywords", document.getKeywords());
        model.addAttribute("tableTitles", document.getTableTitles());
        model.addAttribute("tableHeaders", document.getTableTitles());
        model.addAttribute("userInput", new UserInput());

        return "page1";
        }


    @PostMapping("/page1") //for new ter from user
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes, Model model) throws IOException {
        // check file to exist and be docx type
        if (!FileController.verifyFile(file, ".doc")) {
            message.add("Please select a valid docx file to upload.");
            attributes.addFlashAttribute("message", message);
            return "redirect:/";
        }
//TODO get user input other than file
        // save the file on the local file system
        //TODO - save file to disk if not existent, or save to database?
        String filePath = FileController.saveFile(file, USER_UPLOAD_DIR);

        //set name
        document.setName(file.getName());

        // normalize the file path
        document.setPath(USER_UPLOAD_DIR + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));

        XWPFDocument xwpfDocument = FileController.convertWordToXWPFDocument(filePath);

        //get keywords and store them
        document.setKeywords(DocumentParser.getKeyWords(xwpfDocument, "<change>")); //TODO get rid of hardcoded

        //get table headers and store them
        document.setTables(DocumentParser.getTableHeaders(xwpfDocument));
        document.setTableTitles(DocumentParser.getTableTitles(xwpfDocument)); //added by me


        progressBar = 50;
        errorFlag = true;
        model.addAttribute("progressBar", progressBar);
        model.addAttribute("errorFlag", errorFlag);
        model.addAttribute("message", "You successfully uploaded " + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())) + '!');
        model.addAttribute("keywords", document.getKeywords());
        model.addAttribute("tableTitles", document.getTableTitles());
        model.addAttribute("tableHeaders", document.getTableTitles());
        model.addAttribute("userInput", new UserInput());


        return "page1";
    }

    @GetMapping(value = "/result")
    public String showResult(Model model,@ModelAttribute("userInput") UserInput userInput) {
        errorFlag = true;
        UserKeyword userKeyword = new UserKeyword();
        model.addAttribute("userKeyword", userKeyword);

        String[] keywordsCommaSeparated = userInput.getKeywordsCommaSeparated().split(",");

        //for keyword user input validation. check if all the keywords were added
        String[] removedNull = Arrays.stream(keywordsCommaSeparated)
                .filter(value ->
                        value != null && value.length() > 0
                )
                .toArray(size -> new String[size]);


//        if(document.getKeywords().toArray().length!= removedNull.length){
//            model.addAttribute("message", "You need to enter all the keywords!");
//            return "redirect:/page1";
//        }

        return "result";
    }
    @PostMapping(value = "/result")
    public String submitForm(@ModelAttribute("userInput") UserInput userInput, MultipartFile[] files, Model model,RedirectAttributes attributes) throws Exception {
        message.clear();
        errorFlag = true;
        String result = "result";
        progressBar = 100;
        model.addAttribute("progressBar", progressBar);

        //iterate keywords and populate document object
//        String[] keywordsCommaSeparated = userInput.getKeywordsCommaSeparated().split(",");
        String[] keywordsCommaSeparated = userInput.getKeywordsCommaSeparated().replace(",",", ").split(",");
        //for keyword user input validation. check if all the keywords were added
        String[] removedNull = Arrays.stream(keywordsCommaSeparated)
                .filter(value ->
                        value != null && value.length() > 0
                )
                .toArray(size -> new String[size]);

//
//if(document.getKeywords().toArray().length!= removedNull.length){
//    errorFlag = false;
//    attributes.addFlashAttribute("message", "You didn't insert all the keywords");
//    attributes.addFlashAttribute("errorFlag", errorFlag);
//    return "redirect:/page1";
//
////    if(document.getKeywords().size()!=userInput.getKeywordsCommaSeparated().split(",").length){
////
////    }
//
//}

for(int i=0;i<keywordsCommaSeparated.length;i++){

    if(keywordsCommaSeparated[i].trim().equals("")){
        errorFlag = false;
        message.add("You didnt insert any value for "+document.getKeywords().toArray()[i]);
        attributes.addFlashAttribute("message", message);
    }
}

            for (int i = 0; i < keywordsCommaSeparated.length; i++) {
                document.putKeywordPair(String.valueOf(document.getKeywords().toArray()[i]), keywordsCommaSeparated[i]);
            }

        String[] tableTitleCommaSeparated = userInput.getTableTitleCommaSeparated().split(",");
        userInput.setApisCommaSeparated(userInput.getApisCommaSeparated().replace(",", " ,") + " ");

        String[] apisCommaSeparated = userInput.getApisCommaSeparated().split(",");

        for (int i = 0; i < document.getTableTitles().size(); i++) {
            //TODO make excel object with name, api true/fasle , file
            String filepath = null;

            // case excel is valid, ignore api
            if(!Objects.requireNonNull(files[i].getOriginalFilename()).isEmpty()) {
                if ((FileController.verifyFile(files[i], ".xls") || FileController.verifyFile(files[i], ".xlsx"))) {
                    filepath = FileController.saveFile(files[i], TABLE_UPLOAD_DIR);
                    //case excel is invalid, use api
                } else {
                    errorFlag = false;
                    message.add(files[i].getOriginalFilename() + " is not a valid excel file");
                    attributes.addFlashAttribute("message", message);
                }
            }
            DocumentTable dt = new DocumentTable(document.getTables().get(i), apisCommaSeparated[i], filepath);
            document.addDT(dt);
        }

        if(message.size()>=1){
            return "redirect:/page1";
        }

        //create document
        DocumentGeneratorController.setDocumentTemplate(document.getPath());

        //iterate all keywords and replace them
        for (String key : document.getCompletedKeywords().keySet()) {
            DocumentGeneratorController.replaceTextInAllParagraphs(key, document.getCompletedKeywords().get(key));
        }

        DocumentGeneratorController.dataBindingIdExcel(tableTitleCommaSeparated,files);

        DocumentGeneratorController.saveDocument(RESULT + "modificat.docx");

        //make document null
        document.clear();

        //delete files
        FileController.deleteTempFiles();

        //serve file to user
        File folder = new File(RESULT); // for showing the user the list of templates we have
        File[] listOfFiles = folder.listFiles(); // for showing the user the list of templates we have

        model.addAttribute("files", listOfFiles);
        return result;
    }

    @RequestMapping("/file/{tempfileName}")
    @ResponseBody
    public void show(@PathVariable("tempfileName") String tempFileName, HttpServletResponse response) throws IOException {
        if (tempFileName.contains(".docx")) response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment; filename=" + tempFileName);
        response.setHeader("Content-Transfer-Encoding", "binary");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream((RESULT + tempFileName));
            int len;
            byte[] buf = new byte[1024];
            while ((len = fis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.close();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        model.addAttribute("errorFlag", errorFlag);
        model.addAttribute("keywords", DocumentParser.getKeyWords(document, "<change>"));
        model.addAttribute("tableHeaders", DocumentParser.getTableHeaders(document));
        model.addAttribute("message", "You successfully selected " + fileName + " template!");

        return "extempresult";
    }
}


