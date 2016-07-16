/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import processview.GenerateDataForView;

/**
 *
 * @author apple
 */
public class GenerateViewServlet extends HttpServlet {

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

        String topic_keys_file_path = request.getParameter("topicKeys");
        String word_counts_file_path = request.getParameter("wordCounts");
        String view_styles = request.getParameter("viewStyle");

        File topic_keys_file = new File(topic_keys_file_path);
        File word_count_file = new File(word_counts_file_path);
        if (!topic_keys_file.exists() || !word_count_file.exists()) {
            request.getRequestDispatcher("./error.jsp").forward(request, response);
        } else {

            GenerateDataForView generateDataTool = new GenerateDataForView(topic_keys_file_path, word_counts_file_path);
            generateDataTool.generateDataForView();
            JSONObject namAndSizeJson = generateDataTool.getJson();

            request.setAttribute("sizeJson", namAndSizeJson.toString());
            
            try {
                try (
                        InputStream topicsIn = new FileInputStream(topic_keys_file_path);
                        BufferedReader topicsReader = new BufferedReader(new InputStreamReader(topicsIn))) {

                    JSONObject json = new JSONObject();
                    JSONArray children = new JSONArray();
                    json.put("parent", children);

                    String topicsLine = "";
                    while ((topicsLine = topicsReader.readLine()) != null) {
                        JSONObject topicGroup = new JSONObject();
                        JSONArray topicArray = new JSONArray();
                        topicGroup.put("children", topicArray);

                        String[] topics = topicsLine.split("\\s");
                        for (int i = 0; i < topics.length; ++i) {
                            if (topics.length > 2) {
                                if (!NumberUtils.isNumber(topics[i])) {
                                    JSONObject topic = new JSONObject();
                                    topic.put("name", topics[i]);
                                    topicArray.put(topic);
                                }
                            }
                        }
                        children.put(topicGroup);
                    }

                    request.setAttribute("topicsJson", json.toString());
                } catch (JSONException ex) {
                    Logger.getLogger(GenerateViewServlet.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                System.out.println("IOException: " + ex);
            }
            if (view_styles.equals("Table")) {
                request.getRequestDispatcher("./style_table.jsp").forward(request, response);
            } else if (view_styles.equals("Topics Frequency")) {
                request.getRequestDispatcher("./style_frequency.jsp").forward(request, response);
            } else if (view_styles.equals("Bubble Chart")) {
                request.getRequestDispatcher("./style_bubble.jsp").forward(request, response);
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
