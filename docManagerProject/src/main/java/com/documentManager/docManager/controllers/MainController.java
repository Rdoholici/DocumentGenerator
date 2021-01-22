package com.documentManager.docManager.controllers;

import com.documentManager.docManager.models.Document;
import com.documentManager.docManager.models.DocumentTable;
import com.documentManager.docManager.models.DocumentType;
import com.documentManager.docManager.models.UserInput;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        List<String> listType = Arrays.asList("TER - release", "TER - Hot Fix");//TODO per user entries
        model.addAttribute("listType", listType);
        return "index";
    }


    @PostMapping("/page1") //for new ter from user
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes, Model model) throws IOException {
        // check file to exist and be docx type
        if (!FileController.verifyFile(file, ".doc")) {
            attributes.addFlashAttribute("message", "Please select a valid docx file to upload.");
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

        System.out.println(document.getTables());
        System.out.println(document.getTableTitles());

        Map<String, String> test = IntStream.range(0, document.getTableTitles().size()).boxed()
                .collect(Collectors.toMap(i -> document.getTableTitles().get(i), i -> document.getTables().get(i)));

        for (Map.Entry<String,String> entry : test.entrySet())
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());

        // set the progressbar and errorflag and return success message
        progressBar = 50;
        model.addAttribute("progressBar", progressBar);
        model.addAttribute("errorFlag", errorFlag);
        model.addAttribute("message", "You successfully uploaded " + document.getName() + '!');
        model.addAttribute("keywords", document.getKeywords());
        model.addAttribute("tableTitles", document.getTableTitles());
        model.addAttribute("tableHeaders", document.getTableTitles());
        model.addAttribute("userInput", new UserInput());


        return "page1";
    }

    @GetMapping("/result")
    public String showResult(Model model) {
//        UserKeyword userKeyword = new UserKeyword();
//        model.addAttribute("userKeyword", userKeyword);
        return "result";
    }

    @PostMapping("/result")
    public String submitForm(@ModelAttribute("userInput") UserInput userInput, MultipartFile[] files, Model model) throws Exception {
        progressBar = 100;
        model.addAttribute("progressBar", progressBar);

        //iterate keywords and populate document object
        String[] keywordsCommaSeparated = userInput.getKeywordsCommaSeparated().split(",");
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
            if (FileController.verifyFile(files[i], ".xls")||FileController.verifyFile(files[i], ".xlsx")) {
                filepath = FileController.saveFile(files[i], TABLE_UPLOAD_DIR);
                //case excel is invalid, use api
            } else {
                //use api
            }

            DocumentTable dt = new DocumentTable(document.getTables().get(i), apisCommaSeparated[i], filepath);
            document.addDT(dt);
        }

        //create document
        DocumentGeneratorController.setDocumentTemplate(document.getPath());

        //iterate all keywords and replace them
        for (String key : document.getCompletedKeywords().keySet()) {
            DocumentGeneratorController.replaceTextInAllParagraphs(key, document.getCompletedKeywords().get(key));
        }

        DocumentGeneratorController.dataBindingIdExcel(tableTitleCommaSeparated,files);

//        //iterate all document tables and add them
//        for (DocumentTable documentTable : document.getDocumentTables()) {
//            if (!(documentTable.getFilePath() == null)) {
//                try { //TODO throw exception to UI if table headers of excel and word doc do not match
//                    DocumentGeneratorController.addExcelTableToDocumentTable(documentTable);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else if (!documentTable.getApi().equals(null) || !documentTable.getApi().equals(" ")) {//TODO
//                //use api
//            } else {
//                //do nothing
//            }
//        }

        DocumentGeneratorController.saveDocument(RESULT + "modificat.docx");

        //make documen null
        document.clear();

        //delete files
        FileController.deleteTempFiles();

        //serve file to user
        File folder = new File(RESULT); // for showing the user the list of templates we have
        File[] listOfFiles = folder.listFiles(); // for showing the user the list of templates we have

        model.addAttribute("files", listOfFiles);
        return "result";
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


