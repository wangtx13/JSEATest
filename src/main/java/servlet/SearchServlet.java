/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import xml.parse.ExtractPhraseLabels;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utility.Tools.sortMapByValueWithStringInteger;


/**
 *
 * @author apple
 */
public class SearchServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        boolean findMatchedQuery = false;
        try (PrintWriter out = response.getWriter()) {
            String programRootPath = getServletContext().getInitParameter("program-root-path");
            request.setAttribute("program-root-path", programRootPath);
            String searchQuery = request.getParameter("searchQuery");
            String[] searchQueryList = searchQuery.split(" |,|;");
            ArrayList<Integer> matchedTopicIndex = new ArrayList<>();

            try {
                File topicsKey = new File(programRootPath + "showFile/keys.txt");
                int topicCount = 0;
                StringBuffer matchedQueryBuffer = new StringBuffer();
                Map<String, Integer> matchedTopics = new HashMap<>();
                try (
                        InputStream inTopicKeys = new FileInputStream(topicsKey.getPath());
                        BufferedReader readerTopicKeys = new BufferedReader(new InputStreamReader(inTopicKeys))) {
                    String line = "";
                    while ((line = readerTopicKeys.readLine()) != null) {
                        topicCount++;

                        int matchedDegree = 0;
                        for(String str : searchQueryList) {
                            if(line.contains(str)) {
                                matchedDegree++;
                            }
                        }

                        if(matchedDegree > 0) {
                            matchedTopics.put(line, matchedDegree);
                        }
                    }
                }

                Map<String, Integer> sortedMatchedTopics = sortMapByValueWithStringInteger(matchedTopics);
                for(Map.Entry<String, Integer> entry:sortedMatchedTopics.entrySet()) {
                    String matchedTopicsString = entry.getKey();
                    int matchedDegree = entry.getValue();
                    String[] strPart = matchedTopicsString.split("\t| ");
                    int topicIndex = Integer.parseInt(strPart[0]);
                    if (!matchedTopicIndex.contains(topicIndex)) {
                        matchedTopicIndex.add(topicIndex);
                        matchedQueryBuffer = matchedQueryBuffer.append(topicIndex + "::[TOPIC]::" + matchedTopicsString + "\n");//topic index::[TOPIC]::line
                        findMatchedQuery = true;
                    }
                }

                String phraseLabelFilePath = programRootPath + "showFile/topic-phrases.xml";
                ExtractPhraseLabels extractPhraseLabels = new ExtractPhraseLabels(topicCount);
                String[] allPhraseLabels = extractPhraseLabels.getAllPhraseLabels(phraseLabelFilePath, topicCount);
                for(int i = 0; i < allPhraseLabels.length; ++i) {
                    for(String str:searchQueryList) {
                        if (allPhraseLabels[i].contains(str) && !matchedTopicIndex.contains(i)) {
                            matchedTopicIndex.add(i);
                            matchedQueryBuffer = matchedQueryBuffer.append(i + "::[LABEL]::" + allPhraseLabels[i] + "\n");
                            findMatchedQuery = true;
                        }
                    }
                }

                request.setAttribute("matchedQuery", matchedQueryBuffer.toString());

                request.setAttribute("topicCount", topicCount);

            } catch (FileNotFoundException ex) {
                out.println(ex);
            } catch (IOException ex) {
                out.println(ex);
            }

            if(findMatchedQuery) {
                request.getRequestDispatcher("./searchResults.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("./noResults.jsp").forward(request, response);
            }
        }



    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
