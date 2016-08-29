/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package preprocess;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

import static utility.Tools.writeToFile;

/**
 *
 * @author apple
 */
public class TraversalFiles {
    
    //traversal all files
    public static void fileList(File inputFile, int node, ArrayList<String> path, String outputFilePath, boolean ifGeneral, Map<String, Boolean> libraryTypeCondition, String copyrightInfoContent, Map<String, Integer> documentWordsCountList) {
        node ++;
        File[] files = inputFile.listFiles();
        if (!inputFile.exists()){
            System.out.println("File doesn't exist!");
        } else if(inputFile.isDirectory()) {
            path.add(inputFile.getName());
            
            for (File f : files) {
//                for(int i = 0; i < node - 1; i ++) {
//                    System.out.print(" ");
//                }
//                System.out.print("|-");
//                System.out.println(f.getPath());

                String ext = FilenameUtils.getExtension(f.getName());
                if(ext.equals("java")) {
                    try {
//                        System.out.println(" => extracted");
                        
                        //Get extracted file location and add it to output file name,
                        //in order to avoid files in different folder 
                        //have the same name.
                        String extractedCommentsFilePath = outputFilePath + "/preprocess/" + f.getName() + "-comments.txt";
//                        System.out.println(extractedCommentsFilePath);
                        
                        //create output file for extracted comments
                        File extractedCommentsFile = new File(extractedCommentsFilePath);
                        if(extractedCommentsFile.createNewFile()) {
                            System.out.println("Create successful: " + extractedCommentsFile.getName());
                        } 
                        
                        //extract comments
//                        ParseJavaFile parseJavaFile = new ParseJavaFile(f, extractedCommentsFile, ifGeneral, copyrightInfoContent, libraryTypeCondition, copyrightInfoContent documentWordsCountList, extractedCommentsFile);
//                        parseJavaFile.extractComments();

                        //extract all codes
                        StringBuffer allCodes = new StringBuffer();
                        try (InputStream in = new FileInputStream(f.getPath());
                             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                            String line = "";
                            while((line = reader.readLine())!=null) {
                                allCodes.append(line);
                            }
                        }
                        ParseWords parseWords = new ParseWords(allCodes, ifGeneral, libraryTypeCondition, copyrightInfoContent, documentWordsCountList, extractedCommentsFile);
                        allCodes = parseWords.parseAllWords();
                        writeToFile(allCodes.toString(), extractedCommentsFile);
                    } catch (IOException ex) {
                        Logger.getLogger(TraversalFiles.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if(ext.equals("html")) {
                    try {
//                        System.out.println(" => extracted");

                        //Get extracted file location and add it to output file name,
                        //in order to avoid files in different folder
                        //have the same name.
                        String usefulJavadocFilePath = outputFilePath + "/preprocess/" + f.getName() + "-javadoc.txt";
                        
                        //create output file for usefuljavadoc
                        File usefulJavadocFile = new File(usefulJavadocFilePath);
                        if(usefulJavadocFile.createNewFile()) {
                            System.out.println("Create successful: " + usefulJavadocFile.getName());
                        } 
                        
                        //extract useful javadoc
                        ExtractHTMLContent extractJavadoc = new ExtractHTMLContent(f, usefulJavadocFile, ifGeneral, libraryTypeCondition, copyrightInfoContent, documentWordsCountList, usefulJavadocFile);
                        extractJavadoc.extractHTMLContent();
                    } catch (IOException ex) {
                        Logger.getLogger(TraversalFiles.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.out.println(f.getPath());
                    System.out.println(" isn't a java file or html file.");
                } 
                fileList(f, node, path, outputFilePath, ifGeneral, libraryTypeCondition, copyrightInfoContent, documentWordsCountList);
            }
            path.remove(node - 1);
        }
    }
    
    
}
