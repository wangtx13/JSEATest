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

import keywords.reranking.ReRankingKeywords;
import matrixreader.MatrixReader;
import matrixreader.TopicWordMatrixReader;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.linear.RealMatrix;
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

    public void generateDataForView(String programRootPath) {

        int topicsCount = 0;
        String topicsFileContent = "";
        
        try {
            String outputJsonPath = programRootPath + "Web/topics.json";
            File outputJson = new File(outputJsonPath);
            if (outputJson.createNewFile()) {
                System.out.println(outputJson.getName() + " create successful...");
            }

            try (
                    InputStream topicsIn = new FileInputStream(topicKeysFilePath);
                    BufferedReader topicsReader = new BufferedReader(new InputStreamReader(topicsIn));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputJson))) {

                String readLine = "";
                while((readLine = topicsReader.readLine())!=null) {
                    topicsCount++;
                    topicsFileContent += readLine +"\n";
                }

//                Map<String, Integer> topicMap = new HashMap<>();//<keywords, value>
                MatrixReader topicWordMatrixReader = new TopicWordMatrixReader(wordCountFilePath, topicsCount);
                RealMatrix topicWordMatrix = topicWordMatrixReader.read();
                Map<String, Integer> columnHeaderList = topicWordMatrixReader.getColumnHeaderList();
                ReRankingKeywords reRankingKeywords = new ReRankingKeywords(topicWordMatrix, topicWordMatrix.getRowDimension(), columnHeaderList);

                json.put("name", "topics");

                JSONArray children = new JSONArray();
                json.put("children", children);

                String[] topicsFileLines = topicsFileContent.split("\n");
                for(String line : topicsFileLines) {
                    String[] topics = line.split("\\s");//topics[0] is topic index, topics[1] is parameter value, topics[2]-[n] is topics
                    int topicIndex = Integer.parseInt(topics[0]);

                    JSONObject topicGroup = new JSONObject();//{"name": topic index, "children: [topicArray]}
                    topicGroup.put("name", topicIndex);

                    JSONArray topicArray = new JSONArray();
                    topicGroup.put("children", topicArray);

                    for (int i = 0; i < topics.length; ++i) {
                        if (topics.length > 2) {
                            if (!NumberUtils.isNumber(topics[i])) {
//                                double KR1 = reRankingKeywords.calculateKR1(topics[i], topicIndex);
                                double KR2 = -reRankingKeywords.calculateKR2(topics[i], topicIndex);
                                JSONObject topic = new JSONObject();
                                topic.put("name", topics[i]);
//                                topic.put("size", KR1);
                                topic.put("size", KR2);
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

}
