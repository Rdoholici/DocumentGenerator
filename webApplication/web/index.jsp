<%--
  Created by IntelliJ IDEA.
  User: Florin
  Date: 25.11.2020
  Time: 11:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>My Unit Calculator</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

</head>
<body>
<div class = "container">
<h1>
    Units of Measurements Conversion Calculator
</h1>
    <form action="calculate" method = "post">
        <div class="form-group">
            <label for="iv1">Input value:</label>
           <input type="text" name="userValue" class="form-control" id = "iv1"/>
        </div>

        <div class="form-group">
        <label for="iv2">From unit:</label>
        <select multiple class="form-control" name="fromUnit" id="iv2">
            <option>gram</option>
            <option>kilogram</option>
            <option>miligram</option>
            <option>pound</option>
            <option>ounce</option>
            <option>meter</option>
            <option>kilometer</option>
        </select>
        </div>

        <div class="form-group">
            <label for="iv3">To unit:</label>
            <select multiple class="form-control" name="toUnit" id="iv3">
                <option>gram</option>
                <option>kilogram</option>
                <option>miligram</option>
                <option>pound</option>
                <option>ounce</option>
                <option>meter</option>
                <option>kilometer</option>
            </select>
        </div>

            <button type="submit" class="btn btn-default" value = "Convert">Submit</button>
    </form>
        <table class="table">
            <thead>
            <tr>
                <th>User Value</th>
                <th>From unit</th>
                <th>To unit</th>
                <th>Conversion Value</th>
                <th>Valid Transformation?</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>${userValue}</td>
                <td>${fromUnit}</td>
                <td>${toUnit}</td>
                <td>${conversion}</td>
                <td>${flag}</td>
            </tr>
            </tbody>
        </table>
    </div>

</body>
</html>
