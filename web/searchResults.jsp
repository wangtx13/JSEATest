<%-- 
    Document   : table
    Created on : 2016-8-10, 19:18:20
    Author     : tianxia
--%>

<%@ page import="java.util.Map" %>
<%@ page import="matrixreader.MatrixReader" %>
<%@ page import="matrixreader.DocumentTopicMatrixReader" %>
<%@ page import="javax.xml.parsers.SAXParser" %>
<%@ page import="javax.xml.parsers.SAXParserFactory" %>
<%@ page import="xml.parse.TopicPhraseHandler" %>
<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="javax.xml.parsers.ParserConfigurationException" %>
<%@ page import="java.io.*" %>
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
        <p class="illustrate">If you want to search other key words, please click <a href="search.html">here</a></p>
        <br/>
        <h4>Your search query:</h4>
        <p><%= request.getParameter("searchQuery")%>
        </p>
        <br/>
        <h3>Relative topics:</h3>
        <br/>
        <table class="table table-striped">
            <%
                String searchQuery = request.getParameter("searchQuery");
                int topicCount = (Integer) request.getAttribute("topicCount");
                String programRootPath = request.getAttribute("program-root-path").toString();
                String[] tableResults = request.getAttribute("matchedQuery").toString().split("\n");

                String topicsFilePath = programRootPath + "showFile/keys.txt";
                String phraseLabelFilePath = programRootPath + "showFile/topic-phrases.xml";

                File compositionFile = new File(programRootPath + "showFile/composition.txt");
                MatrixReader docTopicMatrixReader = new DocumentTopicMatrixReader(compositionFile, topicCount);
                Map<Integer, String[]> topDocumentList = docTopicMatrixReader.getTopList();

                for (String topicLine : tableResults) {
                    for (String str : searchQuery.split(" |,|;")) {
                        topicLine = topicLine.replaceAll(str, "<b style='color:red'>" + str + "</b>");
                    }
            %>
            <tr>
                <td>
                    <%
                        String[] topicLinePart = topicLine.split("::");
                        int topicIndex = Integer.parseInt(topicLinePart[0]);
                        if (topicLinePart[1].equals("[TOPIC]")) {
                    %>
                    <p>
                        <b>Topic: </b>
                        <%=topicLinePart[2]%>
                    </p>
                    <p>
                        <b>Labels: </b>
                        <%
                            try {

                                SAXParserFactory factory = SAXParserFactory.newInstance();
                                SAXParser saxParser = factory.newSAXParser();
                                TopicPhraseHandler handler = new TopicPhraseHandler(topicIndex);
                                saxParser.parse(phraseLabelFilePath, handler);
                                String phrasesLabels = handler.getMatchedPhrases();
                                for (String str : searchQuery.split(" |,|;")) {
                                    phrasesLabels = phrasesLabels.replaceAll(str, "<b style='color:red'>" + str + "</b>");
                                }

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
                    } else if (topicLinePart[1].equals("[LABEL]")) {
                        try {
                            try (
                                    InputStream in = new FileInputStream(topicsFilePath);
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                                String line = "";
                                while ((line = reader.readLine()) != null) {
                                    int index = Integer.parseInt(line.split("\t| ")[0]);
                                    if (index == topicIndex) {
                                        for (String str : searchQuery.split(" |,|;")) {
                                            line = line.replaceAll(str, "<b style='color:red'>" + str + "</b>");
                                        }
                    %>
                    <p>
                        <b>Topic: </b>
                        <%=line%>
                    </p>
                    <%
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    %>
                    <p>
                        <b>Labels: </b>
                        <%=topicLinePart[2]%>
                    </p>
                    <%
                        }

                        String[] topDocuments = topDocumentList.get(topicIndex);
                    %>
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
        <p class="pull-right affix-top"><a href="#">Back to top</a></p>
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
