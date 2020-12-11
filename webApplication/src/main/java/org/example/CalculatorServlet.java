package org.example;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/calculate")
public class CalculatorServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        double userValue = Double.parseDouble(request.getParameter("userValue"));
        String fromUnit = request.getParameter("fromUnit");
        String toUnit = request.getParameter("toUnit");
        double conversion = 0d;
        String flag = "Y";

        //weight conversion
        if(fromUnit.toLowerCase().equals("gram")){
            if(toUnit.toLowerCase().equals("kilogram")){
               conversion = userValue/1000;
            }
            else if(toUnit.toLowerCase().equals("miligram")){
                conversion = userValue*1000;
            }
            else if(toUnit.toLowerCase().equals("gram")){
                conversion = userValue;
            }
            else {
                flag = "N";
            }
        }
        else if((fromUnit.toLowerCase().equals("kilogram"))){
            if(toUnit.toLowerCase().equals("gram")){
                conversion = userValue*1000;
            }
            else if(toUnit.toLowerCase().equals("miligram")){
                conversion = userValue*1000000;
            }
            else if(toUnit.toLowerCase().equals("kilogram")){
                conversion = userValue;
            }
            else {
                flag = "N";
            }
        }
        else if((fromUnit.toLowerCase().equals("miligram"))){
            if(toUnit.toLowerCase().equals("gram")){
                conversion = userValue/1000;
            }
            else if(toUnit.toLowerCase().equals("miligram")){
                conversion = userValue;
            }
            else if(toUnit.toLowerCase().equals("kilogram")){
                conversion = userValue/1000000;
            }
            else {
                flag = "N";
            }
        }
        else if((fromUnit.toLowerCase().equals("pound"))){
            if(toUnit.toLowerCase().equals("ounce")){
                conversion = userValue*16;
            }
            else if(toUnit.toLowerCase().equals("pound")){
                conversion = userValue;
            }
            else {
                flag = "N";
            }
        }
        else if((fromUnit.toLowerCase().equals("ounce"))){
            if(toUnit.toLowerCase().equals("pound")){
                conversion = userValue/16;
            }
            else if(toUnit.toLowerCase().equals("ounce")){
                conversion = userValue;
            }
            else {
                flag = "N";
            }
        }
        //length conversion
        else if((fromUnit.toLowerCase().equals("meter"))){
            if(toUnit.toLowerCase().equals("kilometer")){
                conversion = userValue/1000;
            }
            else if(toUnit.toLowerCase().equals("meter")){
                conversion = userValue;
            }
            else {
                flag = "N";
            }
        }
        else if((fromUnit.toLowerCase().equals("kilometer"))){
            if(toUnit.toLowerCase().equals("meter")){
                conversion = userValue*1000;
            }
            else if(toUnit.toLowerCase().equals("kilometer")){
                conversion = userValue;
            }
            else {
                flag = "N";
            }
        }

        request.setAttribute("userValue",userValue);
        request.setAttribute("fromUnit",fromUnit);
        request.setAttribute("toUnit",toUnit);
        request.setAttribute("conversion",conversion);
        request.setAttribute("flag",flag);
        request.getRequestDispatcher("index.jsp").forward(request,response);

    }
}
