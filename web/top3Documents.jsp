<%-- 
    Document   : table
    Created on : 2015-5-23, 21:57:55
    Author     : tianxia
--%>

<%@ page import="java.util.Map" %>
<%@ page import="java.io.File" %>
<%@ page import="matrixreader.MatrixReader" %>
<%@ page import="matrixreader.DocumentTopicMatrixReader" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="./image/analysis.jpg">

    <title>JSEA·Java Software Engineers Assister</title>

    <!-- Bootstrap core CSS -->
    <link href="./css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="./css/show.css" rel="stylesheet">
</head>
<body>
<div class="navbar-wrapper">
    <div class="container">
        <nav class="navbar navbar-inverse navbar-static-top" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <a class="navbar-brand" href="#">JSEA</a>
                </div>
                <div id="navbar" class="navbar-collapse collapse">
                    <ul class="nav navbar-nav">
                        <li><a href="home.html">Home </a></li>
                        <li><a href="help.html">Help </a></li>
                        <li class="active"><a href="show.html">Show </a></li>
                        <li><a href="search.html">Search </a></li>
                    </ul>
                </div>
            </div>
        </nav>

    </div>
</div>
<!-- Marketing messaging and featurettes
   ================================================== -->
<!-- Wrap the rest of the page in another container to center all the content. -->

<div class="container marketing">
    <div class="row featurette show_divider">
        <p class="illustrate">If you want to select again, please click <a href="show.html">here</a></p>
        <br/>
        <h3>Topics:</h3>
        <br/>
        <table class="table table-striped">
            <%
                String topics = request.getAttribute("topics").toString();
                String[] topicsArray = topics.split("\n");
                String[] labelsArray = (String[]) request.getAttribute("labels");
                String programRootPath = request.getAttribute("program-root-path").toString();
                File compositionFile = new File(programRootPath + "showFile/composition.txt");
                MatrixReader docTopicMatrixReader = new DocumentTopicMatrixReader(compositionFile, topicsArray.length);
                Map<Integer, String[]> topDocumentList = docTopicMatrixReader.getTopList();

                for (int i = 0; i < topicsArray.length; ++i) {
                    String[] strPart = topicsArray[i].split("\t| ");
                    int topicIndex = Integer.parseInt(strPart[0]);
                    String[] topDocuments = topDocumentList.get(topicIndex);
            %>
            <tr>
                <td>
                    <p>
                        <b>Topics: </b>
                        <%=topicsArray[i]%>
                    </p>
                    <p>
                        <b>Labels: </b>
                        <%=labelsArray[i]%>
                    </p>
                    <p>
                        <b>Top 3 Documents: </b>
                        <%
                            int index = 0;
                            for (String document : topDocuments) {
                                String[] nameParts = document.split("/");
                                String textName = nameParts[nameParts.length - 1];
                                String fileName = "";
                                int lastIndexOfStrigula = textName.lastIndexOf('-');
                                if (lastIndexOfStrigula >= 0) {
                                    fileName = textName.substring(0, lastIndexOfStrigula);
                                    String link = "http://localhost:8080/static/JSEA/upload/" + fileName;
                                    index++;
                        %>
                        <a href="<%=link%>" target="_blank"><%=fileName%>
                        </a>;
                        <%
                                }
                            }
                            if (index < 3) {
                        %>
                        no more file...
                        <%
                            }
                        %>
                    </p>
                </td>
            </tr>
            <%
                }
            %>
        </table>
    </div>

    <hr class="featurette-divider">

    <!-- /END THE FEATURETTES -->


    <!-- FOOTER -->
    <footer>
        <p class="pull-right"><a href="#">Back to top</a></p>
        <p>2016 @Tianxia, Wang</p>
    </footer>


</div><!-- /.container -->

<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="./js/jquery.min.js"></script>
<script src="./js/bootstrap.min.js"></script>
<script src="./js/docs.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="./js/ie10-viewport-bug-workaround.js"></script>
<!--custom js -->
<script src="./js/home.js"></script>
</body>
</html>
