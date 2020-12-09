package org.example;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/step1")
public class DocumentGenServlet extends HttpServlet {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String message = "";
        String progress = "50";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

          try {
            ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
            List<FileItem> file = sf.parseRequest(request);
            for(FileItem item : file){
                item.write(new File("/Users/Florin/IdeaProjects/webApplication/"+"file_"+timestamp.getTime()+"_"+item.getName()));
            }
            message ="Upload has been done successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            message = "There was an error: " + e.getMessage();
        }

        request.setAttribute("message",message);
        request.setAttribute("progress",progress);

        request.getRequestDispatcher("form_result.jsp").forward(request,response);
    }
}

