<%-- 
    Document   : table
    Created on : 2016-8-10, 19:18:20
    Author     : tianxia
--%>

<%@ page import="java.util.Map" %>
<%@ page import="matrixreader.MatrixReader" %>
<%@ page import="matrixreader.DocumentTopicMatrixReader" %>
<%@ page import="java.io.File" %>
<%@ page import="javax.xml.parsers.SAXParser" %>
<%@ page import="javax.xml.parsers.SAXParserFactory" %>
<%@ page import="xml.parse.TopicPhraseHandler" %>
<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="javax.xml.parsers.ParserConfigurationException" %>
<%@ page import="java.io.IOException" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="tianxia">
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
                        <li><a href="about.html">About </a></li>
                        <li><a href="show.html">Show </a></li>
                        <li class="active"><a href="search.html">Search </a></li>
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
        <h4>Your search query:</h4>
        <p><%= request.getParameter("searchQuery")%>
        </p>
        <br/>
        <h3>Relative topics:</h3>
        <br/>
        <table class="table table-striped">
            <%
                int topicCount = (Integer) request.getAttribute("topicCount");
                String programRootPath = request.getAttribute("program-root-path").toString();
                String[] tableResults = request.getAttribute("matchedQuery").toString().split("\n");

                String phraseLabelFilePath = programRootPath + "search/show_file/topic-phrases.xml";

                String compositionFilePath = programRootPath + "search/show_file/composition.txt";
                File compositionFile = new File(compositionFilePath);
                MatrixReader docTopicMatrixReader = new DocumentTopicMatrixReader(compositionFile, topicCount);
                Map<Integer, String[]> topDocumentList = docTopicMatrixReader.getTopList();

                for (String topicLine : tableResults) {
                    String[] strPart = topicLine.split("\t| ");

            %>
            <tr>
                <td>
                    <p>
                        <b>Topic: </b>
                        <%=topicLine%>
                    </p>
                    <p>
                        <b>Labels: </b>
                        <%
                            int topicIndex = Integer.parseInt(strPart[0]);
                            try {

                                SAXParserFactory factory = SAXParserFactory.newInstance();
                                SAXParser saxParser = factory.newSAXParser();
                                TopicPhraseHandler handler = new TopicPhraseHandler(topicIndex);
                                saxParser.parse(phraseLabelFilePath, handler);
                                String phrasesLabels = handler.getMatchedPhrases();

                        %>
                        <%=phrasesLabels%>
                        <%

                            } catch (SAXException e) {
                                e.printStackTrace();
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        %>
                    </p>
                    <%
                        String[] topDocuments = topDocumentList.get(topicIndex);
                    %>
                    <p>
                        <b>Top 3 Relative Documents:</b>
                        <%
                            for (String document : topDocuments) {
                                String[] nameParts = document.split("/");
                                String fileName = nameParts[nameParts.length - 1];
                        %>
                        <%--<a href="./search/preprocess/<%=fileName%>"><%=fileName%></a>;--%>
                        <%=fileName%>;
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
