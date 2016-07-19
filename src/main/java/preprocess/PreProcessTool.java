/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package preprocess;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import static utility.Tools.createDirectoryIfNotExisting;

/**
 *
 * @author apple
 */
public class PreProcessTool {
    private String inputRootFilePath;
    private String outputFilePath;
    private String timeStampStr;
    private boolean ifGeneral;
    private Map<String, Boolean> libraryTypeCondition;

    public PreProcessTool(String inputRootFilePath, String outputFilePath, boolean ifGeneral, Map<String, Boolean> libraryTypeCondition) {
        this.inputRootFilePath = inputRootFilePath;
        this.outputFilePath = outputFilePath;
        this.timeStampStr = timeStampStr;
        this.ifGeneral = ifGeneral;
        this.libraryTypeCondition = libraryTypeCondition;
    }
    

    public void preProcess() {
        
        //Create a new folder
//        String folderPath = "/Users/wangtianxia1/IdeaProjects/NewProgrammerAssistor/output/PreProcessTool-" + timeStampStr + "/";
        createDirectoryIfNotExisting(outputFilePath);
           
        File inputRootFile = new File(inputRootFilePath);
        ArrayList<String> path = new ArrayList<>();
        if(!inputRootFile.isDirectory()) {
            System.out.println("Please input a extisted directory.");
        } else {
            TraversalFiles.fileList(inputRootFile, 0, path, outputFilePath, ifGeneral, libraryTypeCondition);
        }
        
        
    }
}
