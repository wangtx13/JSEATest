/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import preprocess.PreProcess;
import static utility.Tools.createDir;
import static utility.Tools.randomString;
/**
 *
 * @author apple
 */
public class PreProcessServlet extends HttpServlet {

    private boolean isMultipart;
    private String filePath;
    private int maxFileSize = 50 * 1024 * 1024;
    private int maxMemSize = 4 * 1024;
    private File file;

    public void init() {
        // 获取文件将被存储的位置
        filePath
                = getServletContext().getInitParameter("file-upload");
    }

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
        boolean ifGeneral = false;
        Map<String, Boolean> libraryTypeCondition = new HashMap<String, Boolean>() {
            {
                put("Drawing", false);
                put("Modeling", false);
                put("Need_to_do_2", false);
                put("Need_to_do_3", false);
                put("Need_to_do_4", false);
            }
        };
//        //Add timestamp to folder name
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        String timeStampStr = sdf.format(ts);
//        String timeStampStr = new IPTimeStamp().getIPTimeStampRandom(); 

        //Create a new folder
        String inputRootFilePath = filePath + timeStampStr + "/";
        createDir(inputRootFilePath);

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");

            out.println("<html lang=\"en\">");
            out.println("<head>"
                    + "<meta charset=\"utf-8\">"
                    + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                    + "<meta name=\"description\" content=\"\">"
                    + "<meta name=\"author\" content=\"\">"
                    + "<link rel=\"icon\" href=\"./image/analysis.jpg\">"
                    + "<title>Programmer Assistor</title>"
                    + "<link href=\"./css/bootstrap.min.css\" rel=\"stylesheet\">"
                    + "<script src=\"./js/ie-emulation-modes-warning.js\">"
                    + "</script><!-- Custom styles for this template -->"
                    + "<link href=\"./css/list.css\" rel=\"stylesheet\">"
                    + "</head>");
            out.println("<body>"
                    + "<div class=\"navbar-wrapper\">"
                    + "<div class=\"container\">"
                    + "<nav class=\"navbar navbar-inverse navbar-static-top\" role=\"navigation\">"
                    + "<div class=\"container\">"
                    + "<div class=\"navbar-header\">"
                    + "<a class=\"navbar-brand\" href=\"home.html\">Programmer Assistor</a>"
                    + "</div>"
                    + "<div id=\"navbar\" class=\"navbar-collapse collapse\">"
                    + "<ul class=\"nav navbar-nav\">"
                    + "<li>"
                    + "<a href=\"home.html\">Home </a>"
                    + "</li>"
                    + "<li>"
                    + "<a href=\"help.html\">Help </a>"
                    + "</li>"
                    + "<li>"
                    + "<a href=\"about.html\">About </a>"
                    + "</li>"
                    + "<li>"
                    + "<a href=\"show.html\">Show </a>"
                    + "</li>"
                    + "</ul>"
                    + "</div>"
                    + "</div>"
                    + "</nav>"
                    + "</div>"
                    + "</div>");
            out.println("<div class=\"container marketing\">");
            out.println("<div class=\"row featurette files\" id=\"fileList\">");
            out.println("<h3>MALLET import data directory: </h3>");
            out.println("<p>");
            out.println("./output/PreProcess-" + timeStampStr);
            out.println("</p>");
            out.println("<h3>Uploaded Files: </h3>");

            // 检查有一个文件上传请求
            isMultipart = ServletFileUpload.isMultipartContent(request);

            if (!isMultipart) {
                out.println("<p>the request isn't multipart</p>");
                return;
            }
            DiskFileItemFactory factory = new DiskFileItemFactory();
            // 文件大小的最大值将被存储在内存中
            factory.setSizeThreshold(maxMemSize);
            // Location to save data that is larger than maxMemSize.
            factory.setRepository(new File("/Users/wangtianxia1/IdeaProjects/NewProgrammerAssistor/temp"));
//            factory.setRepository(new File("/Users/u/Documents/NetBeansProject/ProgrammerAssistor/temp"));

            // 创建一个新的文件上传处理程序
            ServletFileUpload upload = new ServletFileUpload(factory);
            // 允许上传的文件大小的最大值
            upload.setSizeMax(maxFileSize);

            try {
                // 解析请求，获取文件项
                List fileItems = upload.parseRequest(request);

                // 处理上传的文件项
                Iterator i = fileItems.iterator();

                while (i.hasNext()) {
                    FileItem fi = (FileItem) i.next();
                    if (!fi.isFormField()) {
                        // 获取上传文件的参数
//                    String fieldName = fi.getFieldName();
                        String fileName = fi.getName();
//                    String contentType = fi.getContentType();
//                    boolean isInMemory = fi.isInMemory();
//                    long sizeInBytes = fi.getSize();
                        // 写入文件 
                        file = new File(inputRootFilePath + randomString(15) + "-"
                                + fileName.substring(fileName.lastIndexOf("/") + 1));
                        fi.write(file);

                        out.println("<p>");
                        out.println(fileName);
                        out.println("</p>");
                    } else {
                        String fieldName = fi.getFieldName();
                        String fieldValue = fi.getString();
                        if (fieldName.equals("general") && fieldValue.equals("on")) {
                            ifGeneral = true;
                        } else if (fieldName.equals("project_type")) {
                            if (fieldValue.equals("Drawing")) {
                                libraryTypeCondition.remove("Drawing");
                                libraryTypeCondition.put("Drawing", true);
                            }  else if (fieldValue.equals("Modeling")) {
                                libraryTypeCondition.remove("Modeling");
                                libraryTypeCondition.put("Modeling", true);
                            }
                        }
//                        out.println("<p>");
//                        out.println(fieldName + ":");
//                        out.println(fieldValue);
//                        out.println("</p>");
                    }
                }
                
                out.println("<h2 id = \"success\" class=\"fileHead\"> Successful Uploading!</h2>");
                out.println("<h2 id = \"success\" class=\"fileHead\"> Successful Proprocessing!</h2>");

            } catch (FileUploadException e) {
                // TODO Auto-generated catch block  
                out.println("The size of files is too much, please upload files less than 50MB");
                e.printStackTrace();
            } catch (Exception ex) {
                System.out.println(ex);

            }

            PreProcess preProcessTool = new PreProcess(inputRootFilePath, timeStampStr, ifGeneral, libraryTypeCondition);
            preProcessTool.preProcess();

            out.println("</div>");
            out.println("<hr class=\"featurette-divider\">");
            out.println("<footer>"
                    + "<p class=\"pull-right\">"
                    + "<a href=\"#\">Back to top</a>"
                    + "</p>"
                    + "<p>2015 @Tianxia, Wang</p>"
                    + "</footer>");
            out.println("</div>");
            out.println("<script src=\"./js/jquery.min.js\"></script>");
            out.println("<script src=\"./js/bootstrap.min.js\"></script>");
            out.println("<script src=\"./js/docs.min.js\"></script>");
            out.println("<script src=\"./js/ie10-viewport-bug-workaround.js\"></script>");
            out.println("<script src=\"./js/home.js\"></script>");
            out.println("</body>");
            out.println("</html>");

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

    // public static boolean createDir(String dirPath) {
    //     File dir = new File(dirPath);
    //     if (dir.exists()) {
    //         System.out.println("The folder has existed");
    //         return false;
    //     }
    //     if (!dirPath.endsWith(File.separator)) {
    //         dirPath = dirPath + File.separator;
    //     }
    //     if (dir.mkdirs()) {
    //         System.out.println("create successful: " + dirPath);
    //         return true;
    //     } else {
    //         System.out.println("create fail...");
    //         return false;
    //     }
    // }

    // public static String randomString(int length) {
    //     String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    //     Random random = new Random();
    //     StringBuffer buf = new StringBuffer();
    //     for (int i = 0; i < length; i++) {
    //         int num = random.nextInt(62);
    //         buf.append(str.charAt(num));
    //     }
    //     return buf.toString();
    // }

}
