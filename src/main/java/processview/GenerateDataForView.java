/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static utility.Tools.randomString;

/**
 *
 * @author apple
 */
public class GenerateDataForView {
    
    private String topicKeysFilePath;
    private String wordCountFilePath;
    private JSONObject json;

    public GenerateDataForView(String topicKeysFilePath, String wordCountFilePath) {
        this.topicKeysFilePath = topicKeysFilePath;
        this.wordCountFilePath = wordCountFilePath;
        json = new JSONObject();
    }

    public JSONObject getJson() {
        return json;
    }

    public void generateDataForView() {
        
        try {
//            System.out.println("topics: ");
//            System.out.println(System.getProperty("user.dir")); 
            String outputJsonPath = "/Users/wangtianxia1/IdeaProjects/NewProgrammerAssistor/Web/topics.json";
//            String outputJsonPath = "/Users/u/Documents/NetBeansProject/ProgrammerAssistor/Web/topics.json";
            File outputJson = new File(outputJsonPath);
            if (outputJson.createNewFile()) {
                System.out.println(outputJson.getName() + " create successful...");
            }

            Map<String, Integer> topicMap = new HashMap<>();

            try (
                    InputStream countIn = new FileInputStream(wordCountFilePath);
                    BufferedReader countReader = new BufferedReader(new InputStreamReader(countIn));
                    InputStream topicsIn = new FileInputStream(topicKeysFilePath);
                    BufferedReader topicsReader = new BufferedReader(new InputStreamReader(topicsIn));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputJson))) {

                String countLine = "";
                while ((countLine = countReader.readLine()) != null) {
                    String[] topics = countLine.split("\t| ");
                    int count = 0;
                    for(int i = 0; i < topics.length; ++i) {
                        if(topics[i].contains(":")) {
                            String[] label = topics[i].split(":");
                            count = count + Integer.parseInt(label[1]);
                        }                      
                    }
 
                    topicMap.put(topics[1], count);

                }
                
                json.put("name", "topics");
                
                JSONArray children = new JSONArray();
                json.put("children", children);

                String topicsLine = "";
                while ((topicsLine = topicsReader.readLine()) != null) {
                    JSONObject topicGroup = new JSONObject();
                    topicGroup.put("name", randomString(8));

                    JSONArray topicArray = new JSONArray(); 
                    topicGroup.put("children", topicArray);

                    String[] topics = topicsLine.split("\\s");
                    for (int i = 0; i < topics.length; ++i) {
                        if (topics.length > 2) {
                            if (!NumberUtils.isNumber(topics[i])) {
                                JSONObject topic = new JSONObject();
                                topic.put("name", topics[i]);
                                topic.put("size", topicMap.get(topics[i]));
                                topicArray.put(topic);
                            }
                        }
                    }
                    
                    children.put(topicGroup);
                }
                
                json.write(writer);
               
                
            } catch (JSONException ex) {
                Logger.getLogger(GenerateDataForView.class.getName()).log(Level.SEVERE, null, ex);
            }            

        } catch (IOException ex) {
            Logger.getLogger(GenerateDataForView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
