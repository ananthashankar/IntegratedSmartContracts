<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sales List</title>



        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

        <!-- jQuery library -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>

        <!-- Latest compiled JavaScript -->
        <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    </head>
    <body>
        <form name="form1" action="ElasticSearch">
            <div class="container">
                <h1> Smart Contracts</h1>
                Select a file to upload:
                <input type="file" id="myFile" size="50">

                <p>Click the button below do the display the file path of the file upload button above (you must select a file first).</p>
                <button type="submit" name="check" id="check">Check The File</button>
                <button type="button" name="btnAjaxCall" id="btnAjaxCall">AjaxCall</button>
                <input type="hidden" name="hiddenPath" value=""/>  
                <c:set var="hiddenPath" value="getHiddenPath()"/>
                <input type="hidden" name="hiddenFileName" value=""/>  
                <input type="hidden" name="page" value="1"/>
                <input type="hidden" name="action" value="getFile"/>
                <p id="demo" name="demo"></p>
                <input type="text"><br>
                <input type="radio" name="Selection" value="ExactSearch"> Exact Search<br>
                <input type="radio" name="Selection" value="definedTerm"> Defined Term Search<br>
                <input type="radio" name="Selection" value="SemanticSearch"> Semantic Search<br>
                <input type="radio" name="Selection" value="SynonymnSearch"> Synonymn Search<br>
                <input type="Submit" value="Submit">
            </div>
        </form>

        <script>
            $(document).ready(function () {
                $("#btnAjaxCall").click(function () {
                    var x = document.getElementById("myFile").value;
                    var y = x.replace(/^.*[\\\/]/, '');
                    var val = y.split('.');
                    document.getElementById("demo").innerHTML = x;
                    var path = x.substring(0, x.lastIndexOf("\\") + 1);
                    document.form1.hiddenPath.value = path;
                    document.form1.hiddenFileName.value = y;

                    alert(path);
                    alert("HiddenPath" + document.form1.hiddenPath.value);
                    $.post("ElasticSearch",
                            {
                                path: path,
                                filename: y
                            },
                            function (data, status) {
                                alert("Data: " + data + "\nStatus: " + status);
                            });
                });
            });
        </script>
    </body>
</html>
