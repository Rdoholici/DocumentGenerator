package org.example;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/step2")
public class DocumentPopulateServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String yourName = request.getParameter("yourName");;
        String typeOfDocument = request.getParameter("typeOfDocument");
        String projectName = request.getParameter("projectName");
        String releaseNumber = request.getParameter("releaseNumber");
        String regionName = request.getParameter("regionName");
        String progress = "100";



        request.setAttribute("progress",progress);
        request.setAttribute("yourName",yourName);
        request.setAttribute("typeOfDocument",typeOfDocument);
        request.setAttribute("projectName",projectName);
        request.setAttribute("releaseNumber",releaseNumber);
        request.setAttribute("regionName",regionName);

        request.getRequestDispatcher("form_result.jsp").forward(request,response);
    }
}

