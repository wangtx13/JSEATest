/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;

/**
 *
 * @author apple
 */
public class ExtractHTMLContent {

    private File inputFile;
    private File outputFile;
    private boolean ifGeneral;
    private Map<String, Boolean> libraryTypeCondition;
    private String copyrightInfoContent;
    private Map<String, Integer> documentWordsCountList;
    private File extractedFile;

    public ExtractHTMLContent(File inputFile, File outputFile, boolean ifGeneral, Map<String, Boolean> libraryTypeCondition, String copyrightInfoContent, Map<String, Integer> documentWordsCountList, File extractedFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.ifGeneral = ifGeneral;
        this.libraryTypeCondition = libraryTypeCondition;
        this.copyrightInfoContent = copyrightInfoContent;
        this.documentWordsCountList = documentWordsCountList;
        this.extractedFile = extractedFile;
    }

    public void extractHTMLContent() {
        String content = "";
        String line = null;
        StringBuffer extractResult = new StringBuffer();
        try {
            try (
                    InputStream in = new FileInputStream(inputFile.getPath());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getPath()))) {
                while ((line = reader.readLine()) != null) {
                    content += line;
                }

                Document doc = Jsoup.parse(content);
                Elements title_content = doc.getElementsByTag("TITLE");
                Elements table_content = doc.getElementsByTag("TABLE");
                Elements pre_content = doc.getElementsByTag("PRE");
                Elements dl_content = doc.getElementsByTag("DL");

                for (Element title : title_content) {
                    String tag = title.tagName();
                    if (tag.equals("title")) {
                        String title_text = title.text();
                        String title_clean = Jsoup.clean(title_text, Whitelist.none());
                        if (title_clean.length() > 0) {
                            extractResult.append(title_clean).append("\r\n");
                        }
                    }

                }

                for (Element table : table_content) {
                    String tag = table.tagName();
                    if (tag.equals("table")) {
                        String table_text = table.text();
                        String table_clean = Jsoup.clean(table_text, Whitelist.none());
                        if (table_clean.length() > 0) {
                            extractResult.append(table_clean).append("\r\n");
                        }
                    }

                }

//                for (Element pre : pre_content) {
//                    String tag = pre.tagName();
//                    if (tag.equals("pre")) {
//                        String pre_text = pre.text();
//                        String pre_clean = Jsoup.clean(pre_text, Whitelist.none());
//                        if (pre_clean.length() > 0) {
//                            extractResult.append(pre_clean).append("\r\n");
//                        }
//                    }
//
//                }

                for (Element dl : dl_content) {
                    String tag = dl.tagName();
                    if (tag.equals("dl")) {
                        String dl_text = dl.text();
                        String dl_clean = Jsoup.clean(dl_text, Whitelist.none());
                        if (dl_clean.length() > 0) {
                            extractResult.append(dl_clean).append("\r\n");
                        }
                    }

                }
                
                ParseWords parseWordsTool = new ParseWords(extractResult, ifGeneral, libraryTypeCondition, copyrightInfoContent, documentWordsCountList, extractedFile);
                extractResult = parseWordsTool.parseAllWords();
                writer.write(extractResult.toString());
                writer.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(ExtractHTMLContent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
