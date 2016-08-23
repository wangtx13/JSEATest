/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import matrixreader.DocumentTopicMatrixReader;
import matrixreader.MatrixReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        try (PrintWriter out = response.getWriter()) {
            String programRootPath = getServletContext().getInitParameter("program-root-path");
            request.setAttribute("program-root-path", programRootPath);
            String searchQuery = request.getParameter("searchQuery");
//            String compositionFilePath = programRootPath + "show_file/composition.txt";

            try {
                File topicsKey = new File(programRootPath + "search/show_file/keys.txt");
                int topicCount = 0;
//            ArrayList<Integer> matchedTopicIndex = new ArrayList<>();
                StringBuffer matchedQueryBuffer = new StringBuffer();
                try (
                        InputStream inTopicKeys = new FileInputStream(topicsKey.getPath());
                        BufferedReader readerTopicKeys = new BufferedReader(new InputStreamReader(inTopicKeys))) {
                    String line = "";
                    while ((line = readerTopicKeys.readLine()) != null) {
                        topicCount++;
                        if (line.contains(searchQuery)) {
                            matchedQueryBuffer = matchedQueryBuffer.append(line + "\n");
//                            String[] linePart = line.split("\t| ");
//                            if(linePart[0]!=null)
//                                matchedTopicIndex.add(Integer.parseInt(linePart[0]));
                        }
                    }

                    out.println("<p>test:" + matchedQueryBuffer.toString() + "</p>");
                    request.setAttribute("matchedQuery", matchedQueryBuffer.toString());
                }

                request.setAttribute("topicCount", topicCount);

                request.getRequestDispatcher("./searchResults.jsp").forward(request, response);
            } catch (FileNotFoundException ex) {
                out.println(ex);
            } catch (IOException ex) {
                out.println(ex);
            }

//            MatrixReader docTopicMatrixReader = new DocumentTopicMatrixReader(compositionFilePath, topicCount);
//            Map<Integer, String[]> topDocumentList = docTopicMatrixReader.getTopList();
//            Map<Integer, String[]> matchedDocumentList = new HashMap<>();
//            for(Map.Entry<Integer, String[]> entry:topDocumentList.entrySet()) {
//                int index = entry.getKey();
//                if(matchedTopicIndex.contains(index)) {
//                    matchedDocumentList.put(index, entry.getValue());
//                }
//            }


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
