<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Document Generator</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>
<body>

<div class="jumbotron jumbotron-fluid">
    <div class="container">
        <h1 class="display-4">Document Generator</h1>
        <p class="lead">With this application, you can automatically generate recurrent documents.</p>
    </div>
</div>

<div class="container">
    <div class="row">
        <div class="col-sm-12">


                 <div class="progress">
                <div class="progress-bar" role="progressbar" aria-valuenow="${progress}" aria-valuemin="${progress}" aria-valuemax="100" style="width:${progress}%">
                    ${progress}
                </div>
            </div>

            <div class="alert alert-success">

                <strong> <h3>${message}</h3></strong>
            </div>
            <h3>Complete the form below with the proper parameters and then click submit.</h3>
            <form action="step2" method = "post">
            <div class="form-group">
                <label for="yourName">Your name:</label>
                <input type="text" class="form-control" id="yourName" name="yourName">
            </div>
            <div class="form-group">
                <label for="typeOfDocument">What type of document is this?</label>
                <select class="form-control" id="typeOfDocument" name="typeOfDocument">
                    <option selected="selected">TER</option>
                    <option>Test Plan</option>
                    <option>Daily Status Report</option>
                    <option>Retrospective Report</option>
                    <option>Sprint Report</option>
                </select>
            </div>
            <div class="form-group">
                <label for="projectName">Project Name:</label>
                <input type="text" class="form-control" id="projectName" name="projectName">
            </div>
            <div class="form-group">
                <label for="releaseNumber">Release Number:</label>
                <input type="text" class="form-control" id="releaseNumber" name="releaseNumber">
            </div>
            <div class="form-group">
                <label for="regionName">Region Name:</label>
                <input type="text" class="form-control" id="regionName" name="regionName">
            </div>
                <button type="submit" class="btn btn-secondary">Submit</button>
            </form>


            <table class="table">
                <thead>
                <tr>
                    <th>Your Name</th>
                    <th>Document Type</th>
                    <th>Project Name</th>
                    <th>Release Number</th>
                    <th>Region Name</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>${yourName}</td>
                    <td>${typeOfDocument}</td>
                    <td>${projectName}</td>
                    <td>${releaseNumber}</td>
                    <td>${regionName}</td>
                </tr>
                </tbody>
            </table>

        </div>
    </div>
</div>

</body>
</html>
